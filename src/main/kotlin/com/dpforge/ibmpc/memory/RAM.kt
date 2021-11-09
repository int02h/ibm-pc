package com.dpforge.ibmpc.memory

interface RAM {
    fun getByte(offset: Int): Int
    fun setByte(offset: Int, value: Int)
}