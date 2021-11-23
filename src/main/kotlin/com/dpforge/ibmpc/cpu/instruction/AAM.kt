package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.FlagsRegister
import com.dpforge.ibmpc.cpu.InterruptException
import com.dpforge.ibmpc.cpu.timing.Timing

object AAM {

    fun aam(cpu: CPU): Int = with(cpu) {
        val b = memory.getByte(codeOffset + 1)
        if (b == 0) {
            throw InterruptException(InterruptException.Type.DIVISION_BY_ZERO)
        }
        val al = registers.al
        registers.ah = al / b
        registers.al = al % b

        alu.updateSignFlag8(registers.al)
        alu.updateZeroFlag8(registers.al)
        alu.updateParityFlag(registers.al)
        registers.flags.setFlag(FlagsRegister.OVERFLOW_FLAG, false)
        registers.flags.setFlag(FlagsRegister.CARRY_FLAG, false)
        registers.flags.setFlag(FlagsRegister.ADJUST_FLAG, false)

        registers.ip += 2
        Timing.aam()
    }

}