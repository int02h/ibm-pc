package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.timing.Timing

object HLT {

    fun hlt(cpu: CPU): Int {
        cpu.haltState = true
        return Timing.hlt()
    }

}
