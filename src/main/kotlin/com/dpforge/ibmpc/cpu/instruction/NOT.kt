package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.timing.Timing

object NOT {

    fun rmw(cpu: CPU): Int = Timing.not(
        cpu.rmw { op -> cpu.alu.not16(op) }
    )

    fun rmb(cpu: CPU): Int = Timing.not(
        cpu.rmb { op -> cpu.alu.not8(op) }
    )

}
