package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.FlagsRegister
import com.dpforge.ibmpc.cpu.Register16
import com.dpforge.ibmpc.cpu.SegmentRegister
import com.dpforge.ibmpc.cpu.timing.Timing.StringOperationTiming

object CompareStrings {

    fun cmpsb(cpu: CPU): Int = cpu.stringOperation(timing = StringOperationTiming.CMPSB, checkZeroFlag = true) {
        val b1 = memory.getByte(makeAddress16(SegmentRegister.DS, Register16.SI))
        val b2 = memory.getByte(makeAddress16IgnoreOverride(SegmentRegister.ES, Register16.DI))
        alu.sub8(b1, b2)
        if (registers.flags.getFlag(FlagsRegister.DIRECTION_FLAG)) {
            registers.si -= 1
            registers.di -= 1
        } else {
            registers.si += 1
            registers.di += 1
        }
    }

    fun cmpsw(cpu: CPU): Int = cpu.stringOperation(timing = StringOperationTiming.CMPSW, checkZeroFlag = true) {
        val w1 = memory.getWord(makeAddress16(SegmentRegister.DS, Register16.SI))
        val w2 = memory.getWord(makeAddress16IgnoreOverride(SegmentRegister.ES, Register16.DI))
        alu.sub16(w1, w2)
        if (registers.flags.getFlag(FlagsRegister.DIRECTION_FLAG)) {
            registers.si -= 2
            registers.di -= 2
        } else {
            registers.si += 2
            registers.di += 2
        }
    }

}
