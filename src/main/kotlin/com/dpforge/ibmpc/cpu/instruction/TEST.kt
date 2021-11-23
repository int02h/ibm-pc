package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.timing.Timing
import com.dpforge.ibmpc.cpu.timing.BinaryInstructionType

object TEST {

    fun rmbRb(cpu: CPU): Int = Timing.test(
        cpu.rmbRb { op1, op2 ->
            cpu.alu.and8(op1, op2)
            BinaryOperation.NO_RESULT
        }
    )

    fun rmwRw(cpu: CPU): Int = Timing.test(
        cpu.rmwRw { op1, op2 ->
            cpu.alu.and16(op1, op2)
            BinaryOperation.NO_RESULT
        }
    )

    fun axIw(cpu: CPU): Int = with(cpu) {
        val w = memory.getWord(codeOffset + 1)
        alu.and16(registers.ax, w)
        registers.ip += 3
        Timing.test(BinaryInstructionType.AccImm)
    }

    fun alIb(cpu: CPU): Int = with(cpu) {
        val b = memory.getByte(codeOffset + 1)
        alu.and8(registers.al, b)
        registers.ip += 2
        Timing.test(BinaryInstructionType.AccImm)
    }

    fun rmwIw(cpu: CPU): Int = Timing.test(
        cpu.rmwIw { op1, op2 ->
            cpu.alu.and16(op1, op2)
            BinaryOperation.NO_RESULT
        }
    )

    fun rmbIb(cpu: CPU): Int = Timing.test(
        cpu.rmbIb { op1, op2 ->
            cpu.alu.and8(op1, op2)
            BinaryOperation.NO_RESULT
        }
    )

}
