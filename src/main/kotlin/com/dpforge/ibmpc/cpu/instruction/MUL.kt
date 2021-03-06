package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.timing.Timing
import com.dpforge.ibmpc.extensions.higherWord
import com.dpforge.ibmpc.extensions.lowerWord

object MUL {

    fun rmw(cpu: CPU): Int = with(cpu) {
        Timing.mul(
            cpu.rmw { op2 ->
                val result = alu.mul16(registers.ax, op2)
                registers.dx = result.higherWord
                registers.ax = result.lowerWord
                UnaryOperation.NO_RESULT
            }
        )
    }

    fun rmb(cpu: CPU): Int = with(cpu) {
        Timing.mul(
            cpu.rmb { op2 ->
                registers.ax = alu.mul8(registers.al, op2)
                UnaryOperation.NO_RESULT
            }
        )
    }

}
