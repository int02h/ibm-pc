package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.timing.Timing
import com.dpforge.ibmpc.cpu.timing.BinaryInstructionType

object SBB {

    fun rmbRb(cpu: CPU): Int = Timing.sbb(
        cpu.rmbRb { op1, op2 -> cpu.alu.sub8(op1, op2, useCarry = true) }
    )

    fun rmwRw(cpu: CPU): Int = Timing.sbb(
        cpu.rmwRw { op1, op2 -> cpu.alu.sub16(op1, op2, useCarry = true) }
    )

    fun rbRmb(cpu: CPU): Int = Timing.sbb(
        cpu.rbRmb { op1, op2 -> cpu.alu.sub8(op1, op2, useCarry = true) }
    )

    fun rwRmw(cpu: CPU): Int = Timing.sbb(
        cpu.rwRmw { op1, op2 -> cpu.alu.sub16(op1, op2, useCarry = true) }
    )

    fun rmwIb(cpu: CPU): Int = Timing.sbb(
        cpu.rmwIb { op1, op2 -> cpu.alu.sub16(op1, op2, useCarry = true) }
    )

    fun rmbIb(cpu: CPU): Int = Timing.sbb(
        cpu.rmbIb { op1, op2 -> cpu.alu.sub8(op1, op2, useCarry = true) }
    )

    fun rmwIw(cpu: CPU): Int = Timing.sbb(
        cpu.rmwIw { op1, op2 -> cpu.alu.sub16(op1, op2, useCarry = true) }
    )

    fun alIb(cpu: CPU): Int = with(cpu) {
        val b = memory.getByte(codeOffset + 1)
        registers.al = alu.sub8(registers.al, b, useCarry = true)
        registers.ip += 2
        Timing.sbb(BinaryInstructionType.AccImm)
    }

    fun axIw(cpu: CPU): Int = with(cpu) {
        val w = memory.getWord(codeOffset + 1)
        registers.ax = alu.sub16(registers.ax, w, useCarry = true)
        registers.ip += 3
        Timing.sbb(BinaryInstructionType.AccImm)
    }

}
