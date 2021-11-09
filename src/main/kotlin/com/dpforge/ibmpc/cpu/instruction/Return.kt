package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU

object Return {

    fun ret(cpu: CPU) = with(cpu) {
        registers.ip = pop16()
    }

    fun retfIw(cpu: CPU) = with(cpu) {
        val w = memory.getWord(codeOffset + 1)
        registers.ip = pop16()
        registers.cs = pop16()
        registers.sp += w
    }

    fun retf(cpu: CPU) = with(cpu) {
        registers.ip = pop16()
        registers.cs = pop16()
    }

    fun retnIw(cpu: CPU) = with(cpu) {
        val w = memory.getWord(codeOffset + 1)
        registers.ip = pop16()
        registers.sp += w
    }

    fun iret(cpu: CPU) = with(cpu) {
        registers.ip = pop16()
        registers.cs = pop16()
        registers.flags.value16 = pop16()
    }

}