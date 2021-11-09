package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU

object RCL {

    fun rmb(cpu: CPU, count: Int) {
        cpu.rmb { op -> cpu.alu.rcl8(op, count) }
    }

    fun rmw(cpu: CPU, count: Int) {
        cpu.rmw { op -> cpu.alu.rcl16(op, count) }
    }

}
