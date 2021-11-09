package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.FlagsRegister

object JE {
    fun short(cpu: CPU) {
        cpu.jumpShortIf { flags.getFlag(FlagsRegister.ZERO_FLAG) }
    }
}