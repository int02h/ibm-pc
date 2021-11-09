package com.dpforge.ibmpc.port

import com.dpforge.ibmpc.extensions.toHex
import org.slf4j.Logger

class IgnoredPort(private val name: String, private val logger: Logger) : Port {

    override fun write(value: Int) {
        logger.debug("Writing ${value.toHex()} into '$name' is ignored")
    }

    override fun read(): Int {
        logger.debug("Reading from '$name' is ignored")
        return 0xFF
    }

}