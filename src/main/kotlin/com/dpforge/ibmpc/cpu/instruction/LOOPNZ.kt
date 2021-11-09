package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.FlagsRegister

object LOOPNZ {

    fun short(cpu: CPU) {
        cpu.jumpShortIf {
            cx -= 1
            cx != 0 && !flags.getFlag(FlagsRegister.ZERO_FLAG)
        }
    }

}