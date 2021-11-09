package com.dpforge.ibmpc

import com.dpforge.ibmpc.port.IgnoredPort
import com.dpforge.ibmpc.port.Port
import com.dpforge.ibmpc.port.PortDevice
import org.slf4j.LoggerFactory

class ProgrammableNoiseGenerator : PortDevice {

    private val logger = LoggerFactory.getLogger("ProgrammableNoiseGenerator")

    override fun getPortMapping(): Map<Int, Port> = mapOf(
        0xC0 to IgnoredPort("TI SN76496 Programmable Tone/Noise Generator (PCjr)", logger)
    )
}