package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.Register16
import com.dpforge.ibmpc.cpu.SegmentRegister

object POP {

    fun reg16(opcode: Int, cpu: CPU) = with(cpu) {
        val reg = Register16.get(opcode - 0x58)
        registers.set(reg, pop16())
        registers.ip += 1
    }

    fun segmentRegister(opcode: Int, cpu: CPU) = with(cpu) {
        val reg = SegmentRegister.get((opcode shr 3) and 0b11)
        registers.set(reg, pop16())
        registers.ip += 1
    }

    fun flags(cpu: CPU) = with(cpu) {
        registers.flags.value16 = pop16()
        registers.ip += 1
    }

    fun rmw(cpu: CPU) {
        cpu.rmw { cpu.pop16() }
    }

}
