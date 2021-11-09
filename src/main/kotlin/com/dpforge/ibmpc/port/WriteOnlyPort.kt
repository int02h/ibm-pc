package com.dpforge.ibmpc.port

abstract class WriteOnlyPort : Port {

    final override fun read(): Int = error("Port is read only")

}