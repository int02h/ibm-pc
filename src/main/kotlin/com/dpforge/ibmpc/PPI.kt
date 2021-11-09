package com.dpforge.ibmpc

import com.dpforge.ibmpc.extensions.toHex
import com.dpforge.ibmpc.port.Port
import com.dpforge.ibmpc.port.PortDevice
import org.slf4j.LoggerFactory

/**
 * Programmable Peripheral Interface, Intel 8255
 */
class PPI(
    private val pic: PIC,
) : PortDevice {

    private val logger = LoggerFactory.getLogger("PPI")

    private val ports = intArrayOf(0x2c, 0, 0, 0)

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
}