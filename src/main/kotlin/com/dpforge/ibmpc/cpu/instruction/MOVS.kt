package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.FlagsRegister
import com.dpforge.ibmpc.cpu.Register16
import com.dpforge.ibmpc.cpu.SegmentRegister

object MOVS {

    fun b(cpu: CPU) = cpu.stringOperation(checkZeroFlag = false) {
        val b = memory.getByte(makeAddress16(SegmentRegister.DS, Register16.SI))
        memory.setByte(makeAddress16IgnoreOverride(SegmentRegister.ES, Register16.DI), b)
        if (registers.flags.getFlag(FlagsRegister.DIRECTION_FLAG)) {
            registers.si -= 1
            registers.di -= 1
        } else {
            registers.si += 1
            registers.di += 1
        }
    }

    fun w(cpu: CPU) = cpu.stringOperation(checkZeroFlag = false) {
        val w = memory.getWord(makeAddress16(SegmentRegister.DS, Register16.SI))
        memory.setWord(makeAddress16IgnoreOverride(SegmentRegister.ES, Register16.DI), w)
        if (registers.flags.getFlag(FlagsRegister.DIRECTION_FLAG)) {
            registers.si -= 2
            registers.di -= 2
        } else {
            registers.si += 2
            registers.di += 2
        }
    }

}
