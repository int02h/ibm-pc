package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.FlagsRegister

object AAS {

    fun aas(cpu: CPU) = with(cpu) {
        if ((registers.al and 0x0F) > 9 || registers.flags.getFlag(FlagsRegister.ADJUST_FLAG)) {
            registers.al = registers.al - 6
            registers.ah = registers.ah - 1
            registers.flags.setFlag(FlagsRegister.ADJUST_FLAG, true)
            registers.flags.setFlag(FlagsRegister.CARRY_FLAG, true)
        } else {
            registers.flags.setFlag(FlagsRegister.ADJUST_FLAG, false)
            registers.flags.setFlag(FlagsRegister.CARRY_FLAG, false)
        }
        registers.al = registers.al and 0x0F

        alu.updateSignFlag8(registers.al)
        alu.updateZeroFlag8(registers.al)
        alu.updateParityFlag(registers.al)

        registers.ip += 1
    }

}