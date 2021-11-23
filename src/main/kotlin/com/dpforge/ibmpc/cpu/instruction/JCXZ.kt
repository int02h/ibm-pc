package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.timing.Timing.ConditionalTiming

object JCXZ {

    fun short(cpu: CPU): Int = cpu.jumpShortIf(ConditionalTiming.JCC) { cx == 0 }

}