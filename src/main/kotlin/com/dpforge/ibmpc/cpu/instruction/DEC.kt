package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.Register16
import com.dpforge.ibmpc.cpu.timing.Timing
import com.dpforge.ibmpc.cpu.timing.UnaryInstructionType

object DEC {

    fun reg16(opcode: Int, cpu: CPU): Int = with(cpu) {
        val reg = Register16.get(opcode - 0x48)
        registers.set(reg, alu.dec16(registers.get(reg)))
        registers.ip += 1
        Timing.dec(UnaryInstructionType.Reg16)
    }

    fun rmb(cpu: CPU): Int = Timing.dec(
        cpu.rmb { op -> cpu.alu.dec8(op) }
    )

    fun rmw(cpu: CPU): Int = Timing.dec(
        cpu.rmw { op -> cpu.alu.dec16(op) }
    )

}
