package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.Register16
import com.dpforge.ibmpc.cpu.SegmentRegister
import com.dpforge.ibmpc.cpu.timing.Timing
import com.dpforge.ibmpc.cpu.timing.UnaryInstructionType

object POP {

    fun reg16(opcode: Int, cpu: CPU): Int = with(cpu) {
        val reg = Register16.get(opcode - 0x58)
        registers.set(reg, pop16())
        registers.ip += 1
        Timing.pop(UnaryInstructionType.Reg16)
    }

    fun segmentRegister(opcode: Int, cpu: CPU): Int = with(cpu) {
        val reg = SegmentRegister.get((opcode shr 3) and 0b11)
        registers.set(reg, pop16())
        registers.ip += 1
        Timing.popSeg()
    }

    fun flags(cpu: CPU): Int = with(cpu) {
        registers.flags.value16 = pop16()
        registers.ip += 1
        Timing.popf()
    }

    fun rmw(cpu: CPU): Int = Timing.pop(
        cpu.rmw { cpu.pop16() }
    )

}
