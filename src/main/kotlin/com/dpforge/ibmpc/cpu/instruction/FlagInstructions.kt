package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.FlagsRegister.Companion.ADJUST_FLAG
import com.dpforge.ibmpc.cpu.FlagsRegister.Companion.CARRY_FLAG
import com.dpforge.ibmpc.cpu.FlagsRegister.Companion.DIRECTION_FLAG
import com.dpforge.ibmpc.cpu.FlagsRegister.Companion.INTERRUPT_ENABLED_FLAG
import com.dpforge.ibmpc.cpu.FlagsRegister.Companion.PARITY_FLAG
import com.dpforge.ibmpc.cpu.FlagsRegister.Companion.SIGN_FLAG
import com.dpforge.ibmpc.cpu.FlagsRegister.Companion.ZERO_FLAG

object FlagInstructions {

    fun cli(cpu: CPU) = with(cpu.registers) {
        flags.setFlag(INTERRUPT_ENABLED_FLAG, false)
        ip += 1
    }

    fun cld(cpu: CPU) = with(cpu.registers) {
        flags.setFlag(DIRECTION_FLAG, false)
        ip += 1
    }

    fun clc(cpu: CPU) = with(cpu.registers) {
        flags.setFlag(CARRY_FLAG, false)
        ip += 1
    }

    fun cmc(cpu: CPU) = with(cpu.registers) {
        flags.setFlag(CARRY_FLAG, !flags.getFlag(CARRY_FLAG))
        ip += 1
    }

    fun stc(cpu: CPU) = with(cpu.registers) {
        flags.setFlag(CARRY_FLAG, true)
        ip += 1
    }

    fun std(cpu: CPU) = with(cpu.registers) {
        flags.setFlag(DIRECTION_FLAG, true)
        ip += 1
    }

    fun sti(cpu: CPU) = with(cpu.registers) {
        flags.setFlag(INTERRUPT_ENABLED_FLAG, true)
        ip += 1
    }

    fun sahf(cpu: CPU) = with(cpu) {
        val affectedFlags = SIGN_FLAG or ZERO_FLAG or ADJUST_FLAG or PARITY_FLAG or CARRY_FLAG
        registers.flags.value16 = registers.ah and affectedFlags
        registers.ip += 1
    }

    fun lahf(cpu: CPU) = with(cpu) {
        registers.ah = registers.flags.value16
        registers.ip += 1
    }

}
