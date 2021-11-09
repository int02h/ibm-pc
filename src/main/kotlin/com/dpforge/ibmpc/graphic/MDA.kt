package com.dpforge.ibmpc.graphic

import com.dpforge.ibmpc.extensions.bit
import com.dpforge.ibmpc.port.IgnoredPort
import com.dpforge.ibmpc.port.Port
import com.dpforge.ibmpc.port.PortDevice
import org.slf4j.LoggerFactory

/**
 * Monochrome Display Adapter
 */
class MDA : PortDevice {

    private val logger = LoggerFactory.getLogger("MDA")

    private var registerIndex = 0
    private val registers = IntArray(0x12) { 0 }

    override fun getPortMapping(): Map<Int, Port> = mapOf(
        0x3B4 to IndexRegister(),
        0x3B5 to DataRegister(),
        0x3B8 to CRTControlPort(),
        0x3B9 to IgnoredPort("color select register on color adapter", logger)
    )

    private inner class IndexRegister : Port {

        override fun write(value: Int) {
            registerIndex = value
        }

        override fun read(): Int = registerIndex

    }

    private inner class DataRegister : Port {

        override fun write(value: Int) {
            if (registerIndex in 0x10..0x11) {
                error("Registers 0x10..0x11 are read only")
            }
            registers[registerIndex] = value
        }

        override fun read(): Int {
            if (registerIndex in 0x00..0x0D) {
                error("Registers 0x00..0x0D are write only")
            }
            return registers[registerIndex]
        }

    }

    private inner class CRTControlPort : Port {

        override fun write(value: Int) {
            val is80x25Text = value.bit(0)
            val enableVideoSignal = value.bit(3)
            val blink = value.bit(5)
            logger.debug("CRT control 80x25 text = $is80x25Text, enable video signal = $enableVideoSignal, blink = $blink")
        }

        override fun read(): Int {
            TODO("Not yet implemented")
        }
    }

}