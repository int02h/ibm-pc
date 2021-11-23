package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.timing.Timing

object NEG {

    fun rmw(cpu: CPU): Int = Timing.neg(
        cpu.rmw { op -> cpu.alu.neg16(op) }
    )

    fun rmb(cpu: CPU): Int = Timing.neg(
        cpu.rmb { op -> cpu.alu.neg8(op) }
    )

}
