package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU

object NOT {

    fun rmw(cpu: CPU) {
        cpu.rmw { op -> cpu.alu.not16(op) }
    }

    fun rmb(cpu: CPU) {
        cpu.rmb { op -> cpu.alu.not8(op) }
    }

}
