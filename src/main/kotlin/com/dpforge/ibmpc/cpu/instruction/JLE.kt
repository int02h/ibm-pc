package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.FlagsRegister
import com.dpforge.ibmpc.cpu.timing.Timing.ConditionalTiming

object JLE {

    fun short(cpu: CPU): Int = cpu.jumpShortIf(ConditionalTiming.JCC) {
        val zeroFlag = flags.getFlag(FlagsRegister.ZERO_FLAG)
        val signFlag = flags.getFlag(FlagsRegister.SIGN_FLAG)
        val overflowFlag = flags.getFlag(FlagsRegister.OVERFLOW_FLAG)
        zeroFlag || (signFlag != overflowFlag)
    }

}
