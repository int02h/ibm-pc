package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU

object TEST {

    fun rmbRb(cpu: CPU) {
        cpu.rmbRb { op1, op2 ->
            cpu.alu.and8(op1, op2)
            BinaryOperation.NO_RESULT
        }
    }

    fun rmwRw(cpu: CPU) {
        cpu.rmwRw { op1, op2 ->
            cpu.alu.and16(op1, op2)
            BinaryOperation.NO_RESULT
        }
    }

    fun axIw(cpu: CPU) = with(cpu) {
        val w = memory.getWord(codeOffset + 1)
        alu.and16(registers.ax, w)
        registers.ip += 3
    }

    fun alIb(cpu: CPU) = with(cpu) {
        val b = memory.getByte(codeOffset + 1)
        alu.and8(registers.al, b)
        registers.ip += 2
    }

    fun rmwIw(cpu: CPU) {
        cpu.rmwIw { op1, op2 ->
            cpu.alu.and16(op1, op2)
            BinaryOperation.NO_RESULT
        }
    }

    fun rmbIb(cpu: CPU) {
        cpu.rmbIb { op1, op2 ->
            cpu.alu.and8(op1, op2)
            BinaryOperation.NO_RESULT
        }
    }

}
