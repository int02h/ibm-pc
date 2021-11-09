package com.dpforge.ibmpc.port

interface PortDevice {

    fun getPortMapping(): Map<Int, Port>

}