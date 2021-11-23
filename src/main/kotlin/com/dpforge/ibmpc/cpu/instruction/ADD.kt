package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.timing.Timing
import com.dpforge.ibmpc.cpu.timing.BinaryInstructionType

object ADD {

    fun rmwIb(cpu: CPU): Int = Timing.add(
        cpu.rmwIb { op1, op2 -> cpu.alu.add16(op1, op2) }
    )

    fun rmbIb(cpu: CPU): Int = Timing.add(
        cpu.rmbIb { op1, op2 -> cpu.alu.add8(op1, op2) }
    )

    fun alIb(cpu: CPU) = with(cpu) {
        val b = memory.getByte(codeOffset + 1)
        registers.al = alu.add8(registers.al, b)
        registers.ip += 2
        Timing.add(BinaryInstructionType.AccImm)
    }

    fun rmwIw(cpu: CPU): Int = Timing.add(
        cpu.rmwIw { op1, op2 -> cpu.alu.add16(op1, op2) }
    )

    fun rmwRw(cpu: CPU): Int = Timing.add(
        cpu.rmwRw { op1, op2 -> cpu.alu.add16(op1, op2) }
    )

    fun rwRmw(cpu: CPU): Int = Timing.add(
        cpu.rwRmw { op1, op2 -> cpu.alu.add16(op1, op2) }
    )

    fun axIw(cpu: CPU) = with(cpu) {
        val w = memory.getWord(codeOffset + 1)
        registers.ax = alu.add16(registers.ax, w)
        registers.ip += 3
        Timing.add(BinaryInstructionType.AccImm)
    }

    fun rbRmb(cpu: CPU): Int = Timing.add(
        cpu.rbRmb { op1, op2 -> cpu.alu.add8(op1, op2) }
    )

    fun rmbRb(cpu: CPU): Int = Timing.add(
        cpu.rmbRb { op1, op2 -> cpu.alu.add8(op1, op2) }
    )

}