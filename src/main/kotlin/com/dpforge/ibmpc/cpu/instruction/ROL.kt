package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU

object ROL {

    fun rmb(cpu: CPU, count: Int) {
        cpu.rmb { op -> cpu.alu.rol8(op, count) }
    }

    fun rmw(cpu: CPU, count: Int) {
        cpu.rmw { op -> cpu.alu.rol16(op, count) }
    }

}