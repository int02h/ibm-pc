package com.dpforge.ibmpc.memory

class ConventionalMemory : RAM {

    private val data = ByteArray(SIZE)

    fun get(offset: Int, length: Int): ByteArray = data.copyOfRange(fromIndex = offset, toIndex = offset + length)

    override fun getByte(offset: Int): Int = data[offset].toInt() and 0xFF

    override fun setByte(offset: Int, value: Int) {
        data[offset] = value.toByte()
    }

    companion object {
        const val SIZE = 640 * 1024
    }
}