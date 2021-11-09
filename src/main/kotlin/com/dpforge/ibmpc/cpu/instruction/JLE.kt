package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.FlagsRegister

object JLE {

    fun short(cpu: CPU) {
        cpu.jumpShortIf {
            val zeroFlag = flags.getFlag(FlagsRegister.ZERO_FLAG)
            val signFlag = flags.getFlag(FlagsRegister.SIGN_FLAG)
            val overflowFlag = flags.getFlag(FlagsRegister.OVERFLOW_FLAG)
            zeroFlag || (signFlag != overflowFlag)
        }
    }

}
