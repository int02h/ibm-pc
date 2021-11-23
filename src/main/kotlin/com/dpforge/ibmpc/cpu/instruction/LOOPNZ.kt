package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.FlagsRegister
import com.dpforge.ibmpc.cpu.timing.Timing.ConditionalTiming

object LOOPNZ {

    fun short(cpu: CPU): Int = cpu.jumpShortIf(ConditionalTiming.LOOPNZ) {
        cx -= 1
        cx != 0 && !flags.getFlag(FlagsRegister.ZERO_FLAG)
    }

}