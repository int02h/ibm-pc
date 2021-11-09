package com.dpforge.ibmpc

import com.dpforge.ibmpc.port.IgnoredPort
import com.dpforge.ibmpc.port.Port
import com.dpforge.ibmpc.port.PortDevice
import org.slf4j.LoggerFactory

class LPT : PortDevice {

    private val logger = LoggerFactory.getLogger("LPT")

    override fun getPortMapping(): Map<Int, Port> = mapOf(
        0x278 to IgnoredPort("LPT3", logger),
        0x378 to IgnoredPort("LPT2", logger),
        0x3BC to IgnoredPort("LPT1", logger)
    )
}