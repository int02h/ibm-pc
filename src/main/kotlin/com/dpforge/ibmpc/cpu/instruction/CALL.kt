package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.AddressingMode
import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.Register16

object CALL {

    fun near(cpu: CPU) = with(cpu) {
        val offset = memory.getWord(codeOffset + 1).toShort()
        registers.ip += 3 // for the CALL itself
        push16(registers.ip)
        registers.ip = registers.ip + offset
    }

    fun far(cpu: CPU) = with(cpu) {
        val offset = codeOffset
        registers.ip += 5 // for the CALL itself
        push16(registers.cs)
        push16(registers.ip)
        registers.ip = memory.getWord(offset + 1)
        registers.cs = memory.getWord(offset + 3)
    }

    fun rmw(cpu: CPU) = with(cpu) {
        val addressingMode = AddressingMode.getForCurrentCodeOffset(this)

        registers.ip += addressingMode.byteCount
        push16(registers.ip)

        registers.ip = when (addressingMode) {
            is AddressingMode.Register -> registers.get(Register16.get(addressingMode.index))
            is AddressingMode.Memory -> memory.getWord(addressingMode.address)
        }
    }

}