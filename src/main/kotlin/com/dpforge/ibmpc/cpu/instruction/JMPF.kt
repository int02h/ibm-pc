package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.AddressingMode
import com.dpforge.ibmpc.cpu.CPU

object JMPF {

    fun iww(cpu: CPU) = with(cpu) {
        val offset = codeOffset
        registers.ip = memory.getWord(offset + 1)
        registers.cs = memory.getWord(offset + 3)
    }

    fun mww(cpu: CPU) = with(cpu) {
        val addressingMode = AddressingMode.getForCurrentCodeOffset(cpu)
        val address = when (addressingMode) {
            is AddressingMode.Register -> error("Register addressing mode is invalid for JMPF")
            is AddressingMode.Memory -> addressingMode.address
        }
        registers.ip = memory.getWord(address)
        registers.cs = memory.getWord(address + 2)
    }

}