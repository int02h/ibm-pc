package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.timing.Timing
import com.dpforge.ibmpc.cpu.timing.BinaryInstructionType

object OR {

    fun rmwRw(cpu: CPU): Int = Timing.or(
        cpu.rmwRw { op1, op2 -> cpu.alu.or16(op1, op2) }
    )

    fun rwRmw(cpu: CPU): Int = Timing.or(
        cpu.rwRmw { op1, op2 -> cpu.alu.or16(op1, op2) }
    )

    fun rbRmb(cpu: CPU): Int = Timing.or(
        cpu.rbRmb { op1, op2 -> cpu.alu.or8(op1, op2) }
    )

    fun rmbRb(cpu: CPU): Int = Timing.or(
        cpu.rmbRb { op1, op2 -> cpu.alu.or8(op1, op2) }
    )

    fun alIb(cpu: CPU): Int = with(cpu) {
        val b = memory.getByte(codeOffset + 1)
        registers.al = alu.or8(registers.al, b)
        registers.ip += 2
        Timing.or(BinaryInstructionType.AccImm)
    }

    fun axIw(cpu: CPU): Int = with(cpu) {
        val w = memory.getWord(codeOffset + 1)
        registers.ax = alu.or16(registers.ax, w)
        registers.ip += 3
        Timing.or(BinaryInstructionType.AccImm)
    }

    fun rmbIb(cpu: CPU): Int = Timing.or(
        cpu.rmbIb { op1, op2 -> cpu.alu.or8(op1, op2) }
    )

    fun rmwIw(cpu: CPU): Int = Timing.or(
        cpu.rmwIw { op1, op2 -> cpu.alu.or16(op1, op2) }
    )

}