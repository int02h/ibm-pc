package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.timing.Timing
import com.dpforge.ibmpc.cpu.timing.BinaryInstructionType

object SUB {

    fun rmwRw(cpu: CPU): Int = Timing.sub(
        cpu.rmwRw { op1, op2 -> cpu.alu.sub16(op1, op2) }
    )

    fun rwRmw(cpu: CPU): Int = Timing.sub(
        cpu.rwRmw { op1, op2 -> cpu.alu.sub16(op1, op2) }
    )

    fun rmbRb(cpu: CPU): Int = Timing.sub(
        cpu.rmbRb { op1, op2 -> cpu.alu.sub8(op1, op2) }
    )

    fun rbRmb(cpu: CPU): Int = Timing.sub(
        cpu.rbRmb { op1, op2 -> cpu.alu.sub8(op1, op2) }
    )

    fun alIb(cpu: CPU): Int = with(cpu) {
        val b = memory.getByte(codeOffset + 1)
        registers.al = alu.sub8(registers.al, b)
        registers.ip += 2
        Timing.sub(BinaryInstructionType.AccImm)
    }

    fun axIw(cpu: CPU): Int = with(cpu) {
        val w = memory.getWord(codeOffset + 1)
        registers.ax = alu.sub16(registers.ax, w)
        registers.ip += 3
        Timing.sub(BinaryInstructionType.AccImm)
    }

    fun rmwIb(cpu: CPU): Int = Timing.sub(
        cpu.rmwIb { op1, op2 -> cpu.alu.sub16(op1, op2) }
    )

    fun rmwIw(cpu: CPU): Int = Timing.sub(
        cpu.rmwIw { op1, op2 -> cpu.alu.sub16(op1, op2) }
    )

    fun rmbIb(cpu: CPU): Int = Timing.sub(
        cpu.rmbIb { op1, op2 -> cpu.alu.sub8(op1, op2) }
    )


}
