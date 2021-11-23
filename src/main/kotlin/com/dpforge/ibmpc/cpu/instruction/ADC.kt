package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.timing.Timing
import com.dpforge.ibmpc.cpu.timing.BinaryInstructionType

object ADC {

    fun rmwRw(cpu: CPU): Int = Timing.adc(
        cpu.rmwRw { op1, op2 -> cpu.alu.add16(op1, op2, useCarry = true) }
    )

    fun rwRmw(cpu: CPU): Int = Timing.adc(
        cpu.rwRmw { op1, op2 -> cpu.alu.add16(op1, op2, useCarry = true) }
    )

    fun axIw(cpu: CPU): Int = with(cpu) {
        val w = memory.getWord(codeOffset + 1)
        registers.ax = alu.add16(registers.ax, w, useCarry = true)
        registers.ip += 3
        Timing.adc(BinaryInstructionType.AccImm)
    }

    fun rmwIb(cpu: CPU): Int = Timing.adc(
        cpu.rmwIb { op1, op2 -> cpu.alu.add16(op1, op2, useCarry = true) }
    )

    fun rmwIw(cpu: CPU): Int = Timing.adc(
        cpu.rmwIw { op1, op2 -> cpu.alu.add16(op1, op2, useCarry = true) }
    )

    fun rmbIb(cpu: CPU): Int = Timing.adc(
        cpu.rmbIb { op1, op2 -> cpu.alu.add8(op1, op2, useCarry = true) }
    )

    fun alIb(cpu: CPU): Int = with(cpu) {
        val b = memory.getByte(codeOffset + 1)
        registers.al = alu.add8(registers.al, b, useCarry = true)
        registers.ip += 2
        Timing.adc(BinaryInstructionType.AccImm)
    }

    fun rbRmb(cpu: CPU): Int = Timing.adc(
        cpu.rbRmb { op1, op2 -> cpu.alu.add8(op1, op2, useCarry = true) }
    )

    fun rmbRb(cpu: CPU): Int = Timing.adc(
        cpu.rmbRb { op1, op2 -> cpu.alu.add8(op1, op2, useCarry = true) }
    )

}
