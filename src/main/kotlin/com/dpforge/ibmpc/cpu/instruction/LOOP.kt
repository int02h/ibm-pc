package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.timing.Timing.ConditionalTiming

object LOOP {

    fun short(cpu: CPU): Int = cpu.jumpShortIf(ConditionalTiming.LOOP) {
        cx -= 1
        cx != 0
    }

}