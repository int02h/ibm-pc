package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU

object HLT {

    fun hlt(cpu: CPU) {
        cpu.haltState = true
    }

}
