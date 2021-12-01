package com.dpforge.ibmpc

import com.dpforge.ibmpc.extensions.bit
import com.dpforge.ibmpc.extensions.bitInt
import com.dpforge.ibmpc.extensions.ensureBitReset
import com.dpforge.ibmpc.extensions.ensureBitSet
import com.dpforge.ibmpc.extensions.exhaustive
import com.dpforge.ibmpc.extensions.toBinary
import com.dpforge.ibmpc.extensions.toHex
import com.dpforge.ibmpc.extensions.withBit
import com.dpforge.ibmpc.port.Port
import com.dpforge.ibmpc.port.PortDevice
import org.slf4j.LoggerFactory

/**
 * Programmable Interrupt Controller, Intel 8259
 * https://stanislavs.org/helppc/8259.html
 */
class PIC : PortDevice {

    private val logger = LoggerFactory.getLogger("PIC")

    private val slaveControllerAttached = BooleanArray(8) { false }

    private var nmiEnabled: Boolean = false

    // Initialization Command Words count
    private var icwCount = 1

    private var baseVectorAddress = 0

    private var icw4Needed = false

    private var interruptMaskRegister = 0

    /**
     * IRR - Interrupt Request Register, maintains a bit vector indicating
     * which IRQ hardware events are awaiting service.
     */
    private var interruptRequestRegister = 0

    /**
     * ISR - In Service Register, tracks IRQ line currently being serviced. Updated by EOI command.
     */
    private var inServiceRegister = 0

    private var commandNextReadAction: CommandNextReadAction? = null

    private var specialMaskMode: SpecialMaskMode? = null

    fun onHardwareInterrupt(irq: Int) {
        interruptRequestRegister = interruptRequestRegister.withBit(irq, true)
    }

    fun getPendingInterrupt(): Int? {
        if (interruptRequestRegister == 0) {
            return null
        }
        for (i in 0..7) {
            if (interruptRequestRegister.bit(i) && !interruptMaskRegister.bit(i)) {
                interruptRequestRegister = interruptRequestRegister.withBit(i, false)
                inServiceRegister = inServiceRegister.withBit(i, true)
                return baseVectorAddress + i
            }
        }
        return null
    }

    override fun getPortMapping(): Map<Int, Port> = mapOf(
        0x20 to CommandRegister(),
        0x21 to MaskRegister(),
        0xA0 to NMIMaskRegister(),
    )

    private inner class CommandRegister : Port {

        override fun write(value: Int) = when (icwCount) {
            1 -> handleICW1(value)
            else -> handleOCW(value)
        }

        override fun read(): Int = when (commandNextReadAction) {
            CommandNextReadAction.InterruptRequestRegister -> interruptRequestRegister
            CommandNextReadAction.InServiceRegister -> inServiceRegister
            null -> error("not selected")
        }

        private fun handleICW1(value: Int) {
            icw4Needed = value.bit(0)
            val mode = Mode.values()[value.bitInt(1)]
            val interruptVectorSize = if (value.bit(2)) 4 else 8
            val triggerMode = TriggerMode.values()[value.bitInt(3)]
            val isICW1 = value.bit(4)
            if (!isICW1) {
                error("Wrong ICW1")
            }
            // bits 5-7 must be zero for PC systems
            icwCount++

            logger.debug(
                "ICW1. Need ICW4 = $icw4Needed" +
                        ", mode = $mode" +
                        ", interrupt vector size = $interruptVectorSize" +
                        ", trigger mode = $triggerMode"
            )
        }

        private fun handleOCW(value: Int) = when ((value shr 3) and 0b11) {
            0b00 -> handleOCW2(value)
            0b01 -> handleOCW3(value)
            else -> error("Unknown OCW")
        }

        private fun handleOCW2(value: Int) {
            val irq = value and 0b111
            value.ensureBitReset(3) { "must be 0 for OCW2" }
            value.ensureBitReset(4) { "must be 0 for OCW2" }
            val eoiType = EOIType.values()[(value shr 5) and 0b111]

            when (eoiType) {
                EOIType.ROTATE_AUTO_CLEAR -> TODO()
                EOIType.NON_SPECIFIC -> { // reset highest priority
                    for (i in 0..7) {
                        if (inServiceRegister.bit(i)) {
                            inServiceRegister = inServiceRegister.withBit(i, false)
                            break
                        }
                    }
                }
                EOIType.NO_OPERATION -> TODO()
                EOIType.SPECIFIC -> inServiceRegister = inServiceRegister.withBit(irq, false)
                EOIType.ROTATE_AUTO_SET -> TODO()
                EOIType.ROTATE_ON_NON_SPECIFIC -> TODO()
                EOIType.SET_PRIORITY -> TODO()
                EOIType.ROTATE_ON_SPECIFIC -> TODO()
            }.exhaustive

            logger.debug("OCW2. IRQ $irq, EOI type = $eoiType")
        }

        private fun handleOCW3(value: Int) {
            if (value.bit(1)) {
                commandNextReadAction = CommandNextReadAction.values()[value.bitInt(0)]
            }
            val pollCommand = value.bit(2)
            value.ensureBitSet(3) { "must be 1 for OCW3" }
            value.ensureBitReset(4) { "must be 0 for OCW3" }
            if (value.bit(6)) {
                specialMaskMode = SpecialMaskMode.values()[value.bitInt(5)]
            }
            // bit 7 reserved (0)

            logger.debug(
                "OCW3. " +
                        "next read action = $commandNextReadAction" +
                        ", poll command = $pollCommand" +
                        ", special mask mode = $specialMaskMode"
            )
        }

    }

    private inner class MaskRegister : Port {

        override fun write(value: Int) = when (icwCount) {
            2 -> handleICW2(value)
            3 -> handleICW3(value)
            4 -> {
                if (icw4Needed) {
                    handleICW4(value)
                } else {
                    handleOCW1(value)
                }
            }
            else -> handleOCW1(value)
        }

        override fun read(): Int = interruptMaskRegister

        private fun handleICW2(value: Int) {
            baseVectorAddress = value and 0b1111_1000
            icwCount++
            logger.debug("ICW2. Base vector address = ${baseVectorAddress.toHex()}")
        }

        private fun handleICW3(value: Int) {
            for (i in 0..7) {
                slaveControllerAttached[i] = value.bit(i)
            }
            icwCount++
            logger.debug("ICW3. Slave controller attached = ${slaveControllerAttached.contentToString()}")
        }

        private fun handleICW4(value: Int) {
            value.ensureBitSet(0) { "Shout be set for 8086/8088 mode" }
            val isAutoEOI = value.bit(1)
            val bufferingMode = if (value.bit(3)) {
                if (value.bit(2)) {
                    BufferingMode.BUFFERED_MASTER
                } else {
                    BufferingMode.BUFFERED_SLAVE
                }
            } else {
                BufferingMode.NON_BUFFERED
            }
            val isSpecialFullyNestedMode = value.bit(4)
            // bit 5-7 reserved

            logger.debug(
                "ICW4. Auto EOI = $isAutoEOI" +
                        ", buffering mode = $bufferingMode" +
                        ", special fully-nested mode = $isSpecialFullyNestedMode"
            )

            icwCount++
            icw4Needed = false
        }

        private fun handleOCW1(value: Int) {
            interruptMaskRegister = value
            logger.debug("OCW1. IMR ${interruptMaskRegister.toBinary()}")
        }

    }

    inner class NMIMaskRegister : Port {

        override fun write(value: Int) {
            if (value == 80) {
                nmiEnabled = true
            } else if (value == 0) {
                nmiEnabled = false
            }
        }

        override fun read(): Int {
            TODO("Not yet implemented")
        }

    }

    private enum class Mode {
        CASCADE,
        SINGLE
    }

    private enum class TriggerMode {
        EDGE,
        LEVEL
    }

    private enum class BufferingMode {
        NON_BUFFERED,
        BUFFERED_SLAVE,
        BUFFERED_MASTER
    }

    private enum class EOIType {
        ROTATE_AUTO_CLEAR,
        NON_SPECIFIC,
        NO_OPERATION,
        SPECIFIC,
        ROTATE_AUTO_SET,
        ROTATE_ON_NON_SPECIFIC,
        SET_PRIORITY,
        ROTATE_ON_SPECIFIC,
    }

    private enum class CommandNextReadAction {
        InterruptRequestRegister,
        InServiceRegister,
    }

    private enum class SpecialMaskMode {
        RESET,
        SET
    }
}