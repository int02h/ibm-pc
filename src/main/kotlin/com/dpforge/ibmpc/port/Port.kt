package com.dpforge.ibmpc.port

interface Port {

    fun write(value: Int)

    fun read(): Int

}