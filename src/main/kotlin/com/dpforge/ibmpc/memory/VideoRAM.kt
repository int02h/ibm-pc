package com.dpforge.ibmpc.memory

class VideoRAM : RAM {

    private val data = ByteArray(SIZE)

    override fun getByte(offset: Int): Int = data[offset].toInt() and 0xFF

    override fun setByte(offset: Int, value: Int) {
        data[offset] = value.toByte()
    }

    companion object {
        const val SIZE = 128 * 1024
    }

}