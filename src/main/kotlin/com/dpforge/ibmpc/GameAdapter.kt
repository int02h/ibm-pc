package com.dpforge.ibmpc

import com.dpforge.ibmpc.port.IgnoredPort
import com.dpforge.ibmpc.port.Port
import com.dpforge.ibmpc.port.PortDevice
import org.slf4j.LoggerFactory

class GameAdapter : PortDevice {

    private val logger = LoggerFactory.getLogger("GameAdapter")

    override fun getPortMapping(): Map<Int, Port> = mapOf(
        0x201 to IgnoredPort("Game Port", logger)
    )
}