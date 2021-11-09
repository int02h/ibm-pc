package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.Register16

object INC {

    fun reg16(opcode: Int, cpu: CPU) = with(cpu) {
        val reg = Register16.get(opcode - 0x40)
        registers.set(reg, alu.inc16(registers.get(reg)))
        registers.ip += 1
    }

    fun rmw(cpu: CPU) {
        cpu.rmw { op -> cpu.alu.inc16(op) }
    }

    fun rmb(cpu: CPU) {
        cpu.rmb { op -> cpu.alu.inc8(op) }
    }

}
