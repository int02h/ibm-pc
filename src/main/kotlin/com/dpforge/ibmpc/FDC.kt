package com.dpforge.ibmpc

import com.dpforge.ibmpc.extensions.bit
import com.dpforge.ibmpc.extensions.toHex
import com.dpforge.ibmpc.extensions.withBit
import com.dpforge.ibmpc.extensions.withBitInt
import com.dpforge.ibmpc.port.Port
import com.dpforge.ibmpc.port.PortDevice
import com.dpforge.ibmpc.port.WriteOnlyPort
import org.slf4j.LoggerFactory

/**
 * Floppy Disk Controller
 * https://stanislavs.org/helppc/765.html
 */
class FDC(
    private val pic: PIC
) : PortDevice {

    private val logger = LoggerFactory.getLogger("FDC")

    private val results = mutableListOf<Int>()

    private var enabled = false
    private var isBusy = false
    private var ioDirection = IODirection.TO_FDC
    private var isDataRegisterReady = false
    private var currentCylinder = 0

    override fun getPortMapping(): Map<Int, Port> = mapOf(
        0x3f2 to DigitalOutputRegister(),
        0x3f4 to StatusRegister(),
        0x3f5 to CommandRegister(),
    )

    private inner class DigitalOutputRegister : WriteOnlyPort() {

        override fun write(value: Int) {
            val disk = value and 0b11
            val fdcEnabled = value.bit(2)
            // bit 3 - diskette DMA enable (reserved PS/2)
            val driveMotorEnabled = booleanArrayOf(value.bit(4), value.bit(5))
            // bit 6-7 - reserved on PS/2

            logger.debug(
                "Digital Output Register. " +
                        "disk = $disk" +
                        ", FDC enabled = $fdcEnabled" +
                        ", drive motor enabled = ${driveMotorEnabled.contentToString()}"
            )

            if (enabled && !fdcEnabled) {
                isDataRegisterReady = true
                pic.onHardwareInterrupt(0x6)
            }

            enabled = fdcEnabled
        }

    }

    private inner class StatusRegister : Port {

        override fun write(value: Int) {
            TODO()
        }

        override fun read(): Int {
            var status = 0
            status = status.withBit(0, false) // drive 0 busy
            status = status.withBit(1, false) // drive 1 busy
            // bit 2 - drive 2 busy (reserved on PS/2)
            // bit 3 - drive 3 busy (reserved on PS/2)
            status = status.withBit(4, isBusy)
            status = status.withBit(5, true) // non-DMA mode
            status = status.withBitInt(6, ioDirection.ordinal)
            status = status.withBit(7, isDataRegisterReady)
            return status
        }

    }

    /**
     * https://www.isdaman.com/alsos/hardware/fdc/floppy.htm
     */
    private inner class CommandRegister : Port {

        override fun write(value: Int) {
            results.clear()
            when (value and 0xF) {
                0x8 -> { // check interrupt status
                    results += buildStatus0()
                    results += currentCylinder
                    ioDirection = IODirection.FROM_FDC
                }
                else -> error("Unsupported command ${(value and 0xF).toHex()}")
            }
        }

        override fun read(): Int {
            val result = results.removeAt(0)
            if (results.isEmpty()) {
                ioDirection = IODirection.TO_FDC
                pic.onHardwareInterrupt(6)
            }
            return result
        }

    }

    private fun buildStatus0(): Int = 0 // TODO

    private enum class IODirection {
        TO_FDC,
        FROM_FDC
    }
}