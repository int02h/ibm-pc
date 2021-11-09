package com.dpforge.ibmpc

import com.dpforge.ibmpc.port.IgnoredPort
import com.dpforge.ibmpc.port.Port
import com.dpforge.ibmpc.port.PortDevice
import org.slf4j.LoggerFactory

class UART : PortDevice {

    private val logger = LoggerFactory.getLogger("UART")

    override fun getPortMapping(): Map<Int, Port> = mapOf(
        0x2FB to IgnoredPort("LCR - Line Control Register", logger),
        0x3FB to IgnoredPort("LCR - Line Control Register", logger)
    )
}