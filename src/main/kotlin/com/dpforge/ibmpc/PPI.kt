package com.dpforge.ibmpc

import com.dpforge.ibmpc.extensions.toHex
import com.dpforge.ibmpc.extensions.withBit
import com.dpforge.ibmpc.port.Port
import com.dpforge.ibmpc.port.PortDevice
import org.slf4j.LoggerFactory

/**
 * Programmable Peripheral Interface, Intel 8255
 */
class PPI(
    private val pic: PIC,
    private val equipment: Equipment,
) : PortDevice {

    private val logger = LoggerFactory.getLogger("PPI")

    private val ports = intArrayOf(getEquipmentSwitches(), 0, 0, 0)

    fun onKeyTyped(scanCode: Int) {
        ports[0] = scanCode
        pic.onHardwareInterrupt(1)
    }

    override fun getPortMapping(): Map<Int, Port> = mapOf(
        0x60 to ValuePort("Port A", 0),
        0x61 to ValuePort("Port B", 1),
        0x62 to ValuePort("Port C", 2),
        0x63 to ValuePort("Control Register", 3)
    )

    private fun getEquipmentSwitches(): Int {
        var result = 0
        result = result.withBit(0, equipment.hasBootDrive)
        result = result.withBit(1, false) // NPU (math coprocessor) present
        result = result or (0b11 shl 2) // memory size (640K)
        result = result or (0b10 shl 4) // 80*25 color (mono mode)
        result = result or (0b00 shl 6) // number of disk drives
        return result
    }

    private inner class ValuePort(val name: String, val index: Int) : Port {
        override fun write(value: Int) {
            logger.debug("Write ${value.toHex()} to $name")
            ports[index] = value
        }

        override fun read(): Int {
            logger.debug("Read ${ports[index].toHex()} from $name")
            return ports[index]
        }
    }

    class Equipment(
        val hasBootDrive: Boolean = false
    )
}