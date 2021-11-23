package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.FlagsRegister
import com.dpforge.ibmpc.cpu.Register16
import com.dpforge.ibmpc.cpu.SegmentRegister
import com.dpforge.ibmpc.cpu.timing.Timing.StringOperationTiming

object STOS {

    fun b(cpu: CPU) = cpu.stringOperation(timing = StringOperationTiming.STOSB, checkZeroFlag = false) {
        val address = makeAddress16IgnoreOverride(SegmentRegister.ES, Register16.DI)
        memory.setByte(address, registers.al)
        if (registers.flags.getFlag(FlagsRegister.DIRECTION_FLAG)) {
            registers.di -= 1
        } else {
            registers.di += 1
        }
    }

    fun w(cpu: CPU) = cpu.stringOperation(timing = StringOperationTiming.STOSW, checkZeroFlag = false) {
        val address = makeAddress16IgnoreOverride(SegmentRegister.ES, Register16.DI)
        memory.setWord(address, registers.ax)
        if (registers.flags.getFlag(FlagsRegister.DIRECTION_FLAG)) {
            registers.di -= 2
        } else {
            registers.di += 2
        }
    }

}