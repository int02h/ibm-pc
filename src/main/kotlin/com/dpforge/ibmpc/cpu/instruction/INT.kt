package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.FlagsRegister
import com.dpforge.ibmpc.cpu.timing.Timing

object INT {

    fun into(cpu: CPU):Int = with(cpu) {
        registers.ip += 1
        if (registers.flags.getFlag(FlagsRegister.OVERFLOW_FLAG)) {
            callSoftwareInterrupt(4)
            Timing.intoCalled()
        } else {
            Timing.intoSkipped()
        }
    }

    fun ib(cpu: CPU):Int = with(cpu) {
        val b = memory.getByte(codeOffset + 1)
        registers.ip += 2
        callSoftwareInterrupt(b)
        Timing.intImm()
    }

}