package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.FlagsRegister

object JL {

    fun short(cpu: CPU) {
        cpu.jumpShortIf {
            flags.getFlag(FlagsRegister.SIGN_FLAG) != flags.getFlag(FlagsRegister.OVERFLOW_FLAG)
        }
    }

}