package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU

object SHR {

    fun rmb(cpu: CPU, count: Int) {
        cpu.rmb { op -> cpu.alu.shr8(op, count) }
    }

    fun rmw(cpu: CPU, count: Int) {
        cpu.rmw { op -> cpu.alu.shr16(op, count) }
    }

}
