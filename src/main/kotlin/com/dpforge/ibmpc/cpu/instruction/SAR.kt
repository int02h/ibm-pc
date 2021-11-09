package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU

object SAR {

    fun rmb(cpu: CPU, count: Int) {
        cpu.rmb { op -> cpu.alu.sar8(op, count) }
    }

    fun rmw(cpu: CPU, count: Int) {
        cpu.rmw { op -> cpu.alu.sar16(op, count) }
    }

}
