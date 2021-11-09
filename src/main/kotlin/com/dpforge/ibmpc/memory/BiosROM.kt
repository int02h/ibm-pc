package com.dpforge.ibmpc.memory

class BiosROM(
    image: ByteArray,
    cassetteBASIC: ByteArray? = null,
) : ROM {

    private val data = ByteArray(SIZE)

    init {
        image.copyInto(destination = data, destinationOffset = SIZE - image.size)
        cassetteBASIC?.let {
            it.copyInto(destination = data, destinationOffset = SIZE - image.size - it.size)
        }
    }

    override fun getByte(offset: Int): Int = data[offset].toInt() and 0xFF

    companion object {
        const val SIZE = 64 * 1024
    }
}