package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU

object NEG {

    fun rmw(cpu: CPU) {
        cpu.rmw { op -> cpu.alu.neg16(op) }
    }

    fun rmb(cpu: CPU) {
        cpu.rmb { op -> cpu.alu.neg8(op) }
    }

}
