package com.dpforge.ibmpc.memory

import com.dpforge.ibmpc.extensions.toHex

/**
 * Memory Layout - http://staff.ustc.edu.cn/~xyfeng/research/cos/resources/machine/mem.htm
 */
class Memory(
    private val conventionalMemory: RAM,
    private val videoRAM: RAM,
    private val bios: ROM,
) {

    fun getByte(offset: Int): Int = when (offset) {
        in 0x00000..0x9FFFF -> conventionalMemory.getByte(offset)
        in 0xA0000..0xBFFFF -> videoRAM.getByte(offset - 0xA0000)
        in 0xC0000..0xC7FFF -> 0//TODO("Video BIOS (32K is typical size)")
        in 0xC8000..0xEFFFF -> 0//error("Access to unused memory ${offset.toHex()}")
        in 0xF0000..0xFFFFF -> bios.getByte(offset - 0xF0000)
        else -> error("Invalid memory address ${offset.toHex()}")
    } and 0xFF

    fun setByte(offset: Int, value: Int): Unit = when (offset) {
        in 0x00000..0x9FFFF -> conventionalMemory.setByte(offset, value)
        in 0xA0000..0xBFFFF -> videoRAM.setByte(offset - 0xA0000, value)
        in 0xC0000..0xC7FFF -> TODO("Video BIOS (32K is typical size)")
        in 0xC8000..0xEFFFF -> error("Access to unused memory ${offset.toHex()}")
        in 0xF0000..0xFFFFF -> error("BIOS is readonly")
        else -> error("Invalid memory address ${offset.toHex()}")
    }

    fun getWord(offset: Int): Int = (getByte(offset) or (getByte(offset + 1) shl 8)) and 0xFFFF

    fun setWord(offset: Int, value: Int) {
        setByte(offset, value and 0xFF)
        setByte(offset + 1, (value shr 8) and 0xFF)
    }

    fun getDoubleWord(offset: Int): Int = getWord(offset) or (getWord(offset + 2) shl 16)

    fun setDoubleWord(offset: Int, value: Int) {
        setWord(offset, value and 0xFFFF)
        setWord(offset + 2, (value shr 16) and 0xFFFF)
    }

}