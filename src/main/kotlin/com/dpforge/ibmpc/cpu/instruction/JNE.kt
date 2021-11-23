package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.FlagsRegister
import com.dpforge.ibmpc.cpu.timing.Timing.ConditionalTiming

object JNE {

    fun short(cpu: CPU): Int = cpu.jumpShortIf(ConditionalTiming.JCC) { !flags.getFlag(FlagsRegister.ZERO_FLAG) }

}