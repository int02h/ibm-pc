package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.FlagsRegister

object INT {

    fun into(cpu: CPU) = with(cpu) {
        registers.ip += 1
        if (registers.flags.getFlag(FlagsRegister.OVERFLOW_FLAG)) {
            callSoftwareInterrupt(4)
        }
    }

    fun ib(cpu: CPU) = with(cpu) {
        val b = memory.getByte(codeOffset + 1)
        registers.ip += 2
        callSoftwareInterrupt(b)
    }

}