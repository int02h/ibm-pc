package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.FlagsRegister
import com.dpforge.ibmpc.cpu.timing.Timing

object DAA {

    fun daa(cpu: CPU):Int = with(cpu) {
        val oldCf = registers.flags.getFlag(FlagsRegister.CARRY_FLAG)
        val oldAl = registers.al

        if ((registers.al and 0x0F) > 9 || registers.flags.getFlag(FlagsRegister.ADJUST_FLAG)) {
            registers.al = oldAl + 6
            if (registers.al < oldAl) { // carry
                registers.flags.setFlag(FlagsRegister.CARRY_FLAG, true)
            }
            registers.flags.setFlag(FlagsRegister.ADJUST_FLAG, true)
        } else {
            registers.flags.setFlag(FlagsRegister.ADJUST_FLAG, false)
        }

        if (oldAl > 0x99 || oldCf) {
            registers.al += 0x60
            registers.flags.setFlag(FlagsRegister.CARRY_FLAG, true)
        } else {
            registers.flags.setFlag(FlagsRegister.CARRY_FLAG, false)
        }

        alu.updateSignFlag8(registers.al)
        alu.updateZeroFlag8(registers.al)
        alu.updateParityFlag(registers.al)

        registers.ip += 1
        Timing.daa()
    }

}
