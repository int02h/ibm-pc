package com.dpforge.ibmpc

import com.dpforge.ibmpc.extensions.toHex
import com.dpforge.ibmpc.port.IgnoredPort
import com.dpforge.ibmpc.port.Port
import com.dpforge.ibmpc.port.PortDevice
import org.slf4j.LoggerFactory

class ExternalDevices : PortDevice {

    private val logger = LoggerFactory.getLogger("ExternalDevices")

    override fun getPortMapping(): Map<Int, Port> {
        val map = mutableMapOf<Int, Port>()

        (0x220..0x26F).forEach { port ->
            map[port] = IgnoredPort("Reserved for I/O channel ${port.toHex()}", logger)
        }

        (0x280..0x2AF).forEach { port ->
            map[port] = IgnoredPort("Reserved for I/O channel ${port.toHex()}", logger)
        }

        (0x340..0x35F).forEach { port ->
            map[port] = IgnoredPort("Reserved for I/O channel ${port.toHex()}", logger)
        }

        return map
    }

}