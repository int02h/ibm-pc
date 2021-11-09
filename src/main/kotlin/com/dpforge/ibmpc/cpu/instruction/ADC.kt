package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU

object ADC {

    fun rmwRw(cpu: CPU) {
        cpu.rmwRw { op1, op2 -> cpu.alu.add16(op1, op2, useCarry = true) }
    }

    fun rwRmw(cpu: CPU) {
        cpu.rwRmw { op1, op2 -> cpu.alu.add16(op1, op2, useCarry = true) }
    }

    fun axIw(cpu: CPU) = with(cpu) {
        val w = memory.getWord(codeOffset + 1)
        registers.ax = alu.add16(registers.ax, w, useCarry = true)
        registers.ip += 3
    }

    fun rmwIb(cpu: CPU) {
        cpu.rmwIb { op1, op2 -> cpu.alu.add16(op1, op2, useCarry = true) }
    }

    fun rmwIw(cpu: CPU) {
        cpu.rmwIw { op1, op2 -> cpu.alu.add16(op1, op2, useCarry = true) }
    }

    fun rmbIb(cpu: CPU) {
        cpu.rmbIb { op1, op2 -> cpu.alu.add8(op1, op2, useCarry = true) }
    }

    fun alIb(cpu: CPU) = with(cpu) {
        val b = memory.getByte(codeOffset + 1)
        registers.al = alu.add8(registers.al, b, useCarry = true)
        registers.ip += 2
    }

    fun rbRmb(cpu: CPU) {
        cpu.rbRmb { op1, op2 -> cpu.alu.add8(op1, op2, useCarry = true) }
    }

    fun rmbRb(cpu: CPU) {
        cpu.rmbRb { op1, op2 -> cpu.alu.add8(op1, op2, useCarry = true) }
    }

}
