package com.dpforge.ibmpc.port

import com.dpforge.ibmpc.extensions.toHex
import com.dpforge.ibmpc.extensions.toHex2

class Ports {

    private val map = mutableMapOf<Int, Port>()

    fun connect(device: PortDevice) {
        device.getPortMapping().forEach { (value, port) ->
            if (map.put(value, port) != null) {
                error("Port $value (${value.toHex()}) is overridden")
            }
        }
    }

    fun write(port: Int, value: Int) {
        getPort(port).write(value)
    }

    fun readByte(port: Int): Int = getPort(port).read() and 0xFF

    fun readWord(port: Int): Int = getPort(port).read() and 0xFFFF

    private fun getPort(port: Int) = map[port] ?: error("No device for port $port (${port.toHex2()})")
}