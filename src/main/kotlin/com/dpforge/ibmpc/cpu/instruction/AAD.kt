package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.FlagsRegister

object AAD {

    fun aad(cpu: CPU) = with(cpu) {
        val b = memory.getByte(codeOffset + 1)
        val al = registers.al
        val ah = registers.ah
        registers.al = al + (ah * b)
        registers.ah = 0

        alu.updateSignFlag8(registers.al)
        alu.updateZeroFlag8(registers.al)
        alu.updateParityFlag(registers.al)
        registers.flags.setFlag(FlagsRegister.OVERFLOW_FLAG, false)
        registers.flags.setFlag(FlagsRegister.CARRY_FLAG, false)
        registers.flags.setFlag(FlagsRegister.ADJUST_FLAG, false)

        registers.ip += 2
    }

}
