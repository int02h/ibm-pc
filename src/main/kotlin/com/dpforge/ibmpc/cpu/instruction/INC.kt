package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.Register16
import com.dpforge.ibmpc.cpu.timing.Timing
import com.dpforge.ibmpc.cpu.timing.UnaryInstructionType

object INC {

    fun reg16(opcode: Int, cpu: CPU): Int = with(cpu) {
        val reg = Register16.get(opcode - 0x40)
        registers.set(reg, alu.inc16(registers.get(reg)))
        registers.ip += 1
        Timing.inc(UnaryInstructionType.Reg16)
    }

    fun rmw(cpu: CPU): Int = Timing.inc(
        cpu.rmw { op -> cpu.alu.inc16(op) }
    )

    fun rmb(cpu: CPU): Int = Timing.inc(
        cpu.rmb { op -> cpu.alu.inc8(op) }
    )

}
