package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU

object RCR {

    fun rmb(cpu: CPU, count: Int) {
        cpu.rmb { op -> cpu.alu.rcr8(op, count) }
    }

    fun rmw(cpu: CPU, count: Int) {
        cpu.rmw { op -> cpu.alu.rcr16(op, count) }
    }

}
