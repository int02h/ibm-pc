package com.dpforge.ibmpc.memory

interface ROM {
    fun getByte(offset: Int): Int
}