package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.AddressingMode
import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.Register16

object JMP {

    fun near(cpu: CPU) = with(cpu) {
        val offset = memory.getWord(codeOffset + 1).toShort()
        registers.ip += 3 // JMP itself
        registers.ip += offset
    }

    fun short(cpu: CPU) = with(cpu) {
        val offset = memory.getByte(codeOffset + 1).toByte()
        registers.ip += 2 // JMP itself
        registers.ip += offset
    }

    fun rmw(cpu: CPU) = with(cpu) {
        val addressingMode = AddressingMode.getForCurrentCodeOffset(this)
        registers.ip = when (addressingMode) {
            is AddressingMode.Register -> registers.get(Register16.get(addressingMode.index))
            is AddressingMode.Memory -> memory.getWord(addressingMode.address)
        }
    }
}