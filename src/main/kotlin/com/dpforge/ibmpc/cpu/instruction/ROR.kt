package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU

object ROR {

    fun rmb(cpu: CPU, count: Int) {
        cpu.rmb { op -> cpu.alu.ror8(op, count) }
    }

    fun rmw(cpu: CPU, count: Int) {
        cpu.rmw { op -> cpu.alu.ror16(op, count) }
    }

}
