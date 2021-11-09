package com.dpforge.ibmpc

import com.dpforge.ibmpc.port.Port
import com.dpforge.ibmpc.port.PortDevice
import com.dpforge.ibmpc.port.WriteOnlyPort
import org.slf4j.LoggerFactory

class ExpansionCard : PortDevice {

    private val logger = LoggerFactory.getLogger("ExpansionCard")

    override fun getPortMapping(): Map<Int, Port> = mapOf(
        0x213 to EnablePort()
    )

    private inner class EnablePort : WriteOnlyPort() {

        override fun write(value: Int) {
            val enable = value == 0
            logger.debug("Expansion unit {}", if (enable) "enable" else "disable")
        }

    }
}