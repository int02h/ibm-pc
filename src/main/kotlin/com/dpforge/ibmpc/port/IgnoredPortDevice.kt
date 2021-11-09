package com.dpforge.ibmpc.port

import org.slf4j.LoggerFactory

class IgnoredPortDevice : PortDevice {

    private val logger = LoggerFactory.getLogger("IgnoredPortDevice")

    override fun getPortMapping(): Map<Int, Port> = mapOf(
        0x2c1 to IgnoredPort("AST-clock (probably)", logger)
    )

}