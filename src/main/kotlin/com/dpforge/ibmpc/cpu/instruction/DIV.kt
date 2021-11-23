package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.timing.Timing

object DIV {

    fun rmw(cpu: CPU): Int = with(cpu) {
        Timing.div(
            cpu.rmw { op2 ->
                val value = (registers.dx shl 16) or registers.ax
                val result = alu.div32by16(value, op2)
                registers.ax = result.quotient
                registers.dx = result.reminder
                UnaryOperation.NO_RESULT
            })
    }

    fun rmb(cpu: CPU): Int = with(cpu) {
        Timing.div(
            cpu.rmb { op2 ->
                val result = alu.div16by8(registers.ax, op2)
                registers.al = result.quotient
                registers.ah = result.reminder
                UnaryOperation.NO_RESULT
            }
        )
    }

}