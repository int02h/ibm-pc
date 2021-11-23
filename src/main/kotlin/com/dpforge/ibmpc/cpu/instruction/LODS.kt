package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.FlagsRegister
import com.dpforge.ibmpc.cpu.Register16
import com.dpforge.ibmpc.cpu.SegmentRegister
import com.dpforge.ibmpc.cpu.timing.Timing.StringOperationTiming

object LODS {

    fun b(cpu: CPU): Int = cpu.stringOperation(timing = StringOperationTiming.LODSB, checkZeroFlag = false) {
        val address = makeAddress16(SegmentRegister.DS, registers.get(Register16.SI))
        registers.al = memory.getByte(address)
        if (registers.flags.getFlag(FlagsRegister.DIRECTION_FLAG)) {
            registers.si -= 1
        } else {
            registers.si += 1
        }
    }

    fun w(cpu: CPU): Int = cpu.stringOperation(timing = StringOperationTiming.LODSW, checkZeroFlag = false) {
        val address = makeAddress16(SegmentRegister.DS, registers.get(Register16.SI))
        registers.ax = memory.getWord(address)
        if (registers.flags.getFlag(FlagsRegister.DIRECTION_FLAG)) {
            registers.si -= 2
        } else {
            registers.si += 2
        }
    }

}