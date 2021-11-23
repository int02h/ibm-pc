package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.FlagsRegister.Companion.ADJUST_FLAG
import com.dpforge.ibmpc.cpu.FlagsRegister.Companion.CARRY_FLAG
import com.dpforge.ibmpc.cpu.FlagsRegister.Companion.DIRECTION_FLAG
import com.dpforge.ibmpc.cpu.FlagsRegister.Companion.INTERRUPT_ENABLED_FLAG
import com.dpforge.ibmpc.cpu.FlagsRegister.Companion.PARITY_FLAG
import com.dpforge.ibmpc.cpu.FlagsRegister.Companion.SIGN_FLAG
import com.dpforge.ibmpc.cpu.FlagsRegister.Companion.ZERO_FLAG
import com.dpforge.ibmpc.cpu.timing.Timing

object FlagInstructions {

    fun cli(cpu: CPU): Int = with(cpu.registers) {
        flags.setFlag(INTERRUPT_ENABLED_FLAG, false)
        ip += 1
        Timing.cli()
    }

    fun cld(cpu: CPU): Int = with(cpu.registers) {
        flags.setFlag(DIRECTION_FLAG, false)
        ip += 1
        Timing.cld()
    }

    fun clc(cpu: CPU): Int = with(cpu.registers) {
        flags.setFlag(CARRY_FLAG, false)
        ip += 1
        Timing.clc()
    }

    fun cmc(cpu: CPU): Int = with(cpu.registers) {
        flags.setFlag(CARRY_FLAG, !flags.getFlag(CARRY_FLAG))
        ip += 1
        Timing.cmc()
    }

    fun stc(cpu: CPU): Int = with(cpu.registers) {
        flags.setFlag(CARRY_FLAG, true)
        ip += 1
        Timing.stc()
    }

    fun std(cpu: CPU): Int = with(cpu.registers) {
        flags.setFlag(DIRECTION_FLAG, true)
        ip += 1
        Timing.std()
    }

    fun sti(cpu: CPU): Int = with(cpu.registers) {
        flags.setFlag(INTERRUPT_ENABLED_FLAG, true)
        ip += 1
        Timing.sti()
    }

    fun sahf(cpu: CPU): Int = with(cpu) {
        val affectedFlags = SIGN_FLAG or ZERO_FLAG or ADJUST_FLAG or PARITY_FLAG or CARRY_FLAG
        registers.flags.value16 = registers.ah and affectedFlags
        registers.ip += 1
        Timing.sahf()
    }

    fun lahf(cpu: CPU): Int = with(cpu) {
        registers.ah = registers.flags.value16
        registers.ip += 1
        Timing.lahf()
    }

}
