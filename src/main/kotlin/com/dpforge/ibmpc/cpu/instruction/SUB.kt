package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU

object SUB {

    fun rmwRw(cpu: CPU) {
        cpu.rmwRw { op1, op2 -> cpu.alu.sub16(op1, op2) }
    }

    fun rwRmw(cpu: CPU) {
        cpu.rwRmw { op1, op2 -> cpu.alu.sub16(op1, op2) }
    }

    fun rmbRb(cpu: CPU) {
        cpu.rmbRb { op1, op2 -> cpu.alu.sub8(op1, op2) }
    }

    fun rbRmb(cpu: CPU) {
        cpu.rbRmb { op1, op2 -> cpu.alu.sub8(op1, op2) }
    }

    fun alIb(cpu: CPU) = with(cpu) {
        val b = memory.getByte(codeOffset + 1)
        registers.al = alu.sub8(registers.al, b)
        registers.ip += 2
    }

    fun axIw(cpu: CPU) = with(cpu) {
        val w = memory.getWord(codeOffset + 1)
        registers.ax = alu.sub16(registers.ax, w)
        registers.ip += 3
    }

    fun rmwIb(cpu: CPU) {
        cpu.rmwIb { op1, op2 -> cpu.alu.sub16(op1, op2) }
    }

    fun rmwIw(cpu: CPU) {
        cpu.rmwIw { op1, op2 -> cpu.alu.sub16(op1, op2) }
    }

    fun rmbIb(cpu: CPU) {
        cpu.rmbIb { op1, op2 -> cpu.alu.sub8(op1, op2) }
    }


}
