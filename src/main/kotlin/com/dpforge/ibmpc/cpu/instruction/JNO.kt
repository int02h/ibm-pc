package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.FlagsRegister

object JNO {

    fun short(cpu: CPU) {
        cpu.jumpShortIf { !flags.getFlag(FlagsRegister.OVERFLOW_FLAG) }
    }

}