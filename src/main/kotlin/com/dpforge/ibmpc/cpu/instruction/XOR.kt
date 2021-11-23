package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.timing.Timing
import com.dpforge.ibmpc.cpu.timing.BinaryInstructionType

object XOR {

    fun rmbRb(cpu: CPU): Int = Timing.xor(
        cpu.rmbRb { op1, op2 -> cpu.alu.xor8(op1, op2) }
    )

    fun rmwRw(cpu: CPU): Int = Timing.xor(
        cpu.rmwRw { op1, op2 -> cpu.alu.xor16(op1, op2) }
    )

    fun rwRmw(cpu: CPU): Int = Timing.xor(
        cpu.rwRmw { op1, op2 -> cpu.alu.xor16(op1, op2) }
    )

    fun rbRmb(cpu: CPU): Int = Timing.xor(
        cpu.rbRmb { op1, op2 -> cpu.alu.xor8(op1, op2) }
    )

    fun alIb(cpu: CPU): Int = with(cpu) {
        val b = memory.getByte(codeOffset + 1)
        registers.al = alu.xor8(registers.al, b)
        registers.ip += 2
        Timing.xor(BinaryInstructionType.AccImm)
    }

    fun axIw(cpu: CPU): Int = with(cpu) {
        val w = memory.getWord(codeOffset + 1)
        registers.ax = alu.xor16(registers.ax, w)
        registers.ip += 3
        Timing.xor(BinaryInstructionType.AccImm)
    }

    fun rmbIb(cpu: CPU): Int = Timing.xor(
        cpu.rmbIb { op1, op2 -> cpu.alu.xor8(op1, op2) }
    )

    fun rmwIw(cpu: CPU): Int = Timing.xor(
        cpu.rmwIw { op1, op2 -> cpu.alu.xor16(op1, op2) }
    )

}