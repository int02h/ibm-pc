package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.timing.Timing
import com.dpforge.ibmpc.cpu.timing.BinaryInstructionType

object AND {
    fun alIb(cpu: CPU): Int = with(cpu) {
        val b = memory.getByte(codeOffset + 1)
        registers.al = alu.and8(registers.al, b)
        registers.ip += 2
        Timing.and(BinaryInstructionType.AccImm)
    }

    fun rmwRw(cpu: CPU): Int = Timing.and(
        cpu.rmwRw { op1, op2 -> cpu.alu.and16(op1, op2) }
    )

    fun rmwIb(cpu: CPU): Int = Timing.and(
        // undocumented for 8086
        cpu.rmwIb { op1, op2 -> cpu.alu.and16(op1, op2) }
    )

    fun rwRmw(cpu: CPU): Int = Timing.and(
        cpu.rwRmw { op1, op2 -> cpu.alu.and16(op1, op2) }
    )

    fun axIw(cpu: CPU): Int = with(cpu) {
        val w = memory.getWord(codeOffset + 1)
        registers.ax = alu.and16(registers.ax, w)
        registers.ip += 3
        Timing.and(BinaryInstructionType.AccImm)
    }

    fun rmwIw(cpu: CPU): Int = Timing.and(
        cpu.rmwIw { op1, op2 -> cpu.alu.and16(op1, op2) }
    )

    fun rmbRb(cpu: CPU): Int = Timing.and(
        cpu.rmbRb { op1, op2 -> cpu.alu.and8(op1, op2) }
    )

    fun rbRmb(cpu: CPU): Int = Timing.and(
        cpu.rbRmb { op1, op2 -> cpu.alu.and8(op1, op2) }
    )

    fun rmbIb(cpu: CPU): Int = Timing.and(
        cpu.rmbIb { op1, op2 -> cpu.alu.and8(op1, op2) }
    )
}