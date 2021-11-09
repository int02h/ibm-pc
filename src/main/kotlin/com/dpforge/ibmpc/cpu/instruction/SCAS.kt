package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.FlagsRegister
import com.dpforge.ibmpc.cpu.Register16
import com.dpforge.ibmpc.cpu.SegmentRegister

object SCAS {

    fun b(cpu: CPU) = cpu.stringOperation(checkZeroFlag = true) {
        val address = makeAddress16IgnoreOverride(SegmentRegister.ES, Register16.DI)
        alu.sub8(registers.al, memory.getByte(address))
        if (registers.flags.getFlag(FlagsRegister.DIRECTION_FLAG)) {
            registers.di -= 1
        } else {
            registers.di += 1
        }
    }

    fun w(cpu: CPU) = cpu.stringOperation(checkZeroFlag = true) {
        val address = makeAddress16IgnoreOverride(SegmentRegister.ES, Register16.DI)
        alu.sub16(registers.ax, memory.getWord(address))
        if (registers.flags.getFlag(FlagsRegister.DIRECTION_FLAG)) {
            registers.di -= 2
        } else {
            registers.di += 2
        }
    }

}