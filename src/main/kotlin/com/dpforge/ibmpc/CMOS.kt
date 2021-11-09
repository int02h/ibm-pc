package com.dpforge.ibmpc

import com.dpforge.ibmpc.extensions.toHex
import com.dpforge.ibmpc.extensions.toHex2
import com.dpforge.ibmpc.port.Port
import com.dpforge.ibmpc.port.PortDevice
import com.dpforge.ibmpc.port.WriteOnlyPort
import org.slf4j.LoggerFactory

class CMOS : PortDevice {

    private val logger = LoggerFactory.getLogger(CMOS::class.java)

    private var index: Int = 0
    private var nmiDisabled: Boolean = false

    private var shutdownStatusByte: Int = 0

    override fun getPortMapping(): Map<Int, Port> = mapOf(
        0x70 to IndexPort(),
        0x71 to DataPort()
    )

    private inner class IndexPort : WriteOnlyPort() {

        override fun write(value: Int) {
            index = value and 0b1111111
            nmiDisabled = (value and 0b10000000) > 0
            logger.debug("set index $index (${index.toHex2()}) and NMI disabled $nmiDisabled")
        }

    }

    private inner class DataPort : Port {
        override fun write(value: Int) {
            logger.debug("write $value to $index (${index.toHex2()})")
            when (index) {
                SHUTDOWN_STATUS_BYTE -> shutdownStatusByte = value and 0xFF
                else -> error("Writing at unsupported CMOS index ${index.toHex()}")
            }
        }

        override fun read(): Int {
            val value = when (index) {
                SHUTDOWN_STATUS_BYTE -> shutdownStatusByte
                else -> error("Reading at unsupported CMOS index ${index.toHex()}")
            }
            logger.debug("read $value from $index (${index.toHex2()})")
            return value
        }

    }

    private companion object {
        const val SHUTDOWN_STATUS_BYTE = 0x0F
    }
}
