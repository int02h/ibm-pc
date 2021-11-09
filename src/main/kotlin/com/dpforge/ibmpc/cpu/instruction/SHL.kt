package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU

object SHL {

    fun rmb(cpu: CPU, count: Int) {
        cpu.rmb { op -> cpu.alu.shl8(op, count)}
    }

    fun rmw(cpu: CPU, count: Int) {
        cpu.rmw { op -> cpu.alu.shl16(op, count)}
    }

}
