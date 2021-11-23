package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.AddressingMode
import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.Register16
import com.dpforge.ibmpc.cpu.timing.Timing
import com.dpforge.ibmpc.cpu.timing.JmpType

object JMP {

    fun near(cpu: CPU): Int = with(cpu) {
        val offset = memory.getWord(codeOffset + 1).toShort()
        registers.ip += 3 // JMP itself
        registers.ip += offset
        Timing.jmp(JmpType.Near)
    }

    fun short(cpu: CPU): Int = with(cpu) {
        val offset = memory.getByte(codeOffset + 1).toByte()
        registers.ip += 2 // JMP itself
        registers.ip += offset
        Timing.jmp(JmpType.Short)
    }

    fun rmw(cpu: CPU): Int = with(cpu) {
        val instructionType: JmpType
        val addressingMode = AddressingMode.getForCurrentCodeOffset(this)
        registers.ip = when (addressingMode) {
            is AddressingMode.Register -> {
                instructionType = JmpType.Reg16
                registers.get(Register16.get(addressingMode.index))
            }
            is AddressingMode.Memory -> {
                instructionType = JmpType.Mem16(addressingMode)
                memory.getWord(addressingMode.address)
            }
        }
        Timing.jmp(instructionType)
    }
}