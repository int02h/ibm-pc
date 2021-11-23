package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.timing.Timing

object Return {

    fun ret(cpu: CPU): Int = with(cpu) {
        registers.ip = pop16()
        Timing.retn()
    }

    fun retfIw(cpu: CPU): Int = with(cpu) {
        val w = memory.getWord(codeOffset + 1)
        registers.ip = pop16()
        registers.cs = pop16()
        registers.sp += w
        Timing.retfImm()
    }

    fun retf(cpu: CPU): Int = with(cpu) {
        registers.ip = pop16()
        registers.cs = pop16()
        Timing.retf()
    }

    fun retnIw(cpu: CPU): Int = with(cpu) {
        val w = memory.getWord(codeOffset + 1)
        registers.ip = pop16()
        registers.sp += w
        Timing.retnImm()
    }

    fun iret(cpu: CPU): Int = with(cpu) {
        registers.ip = pop16()
        registers.cs = pop16()
        registers.flags.value16 = pop16()
        Timing.iret()
    }

}