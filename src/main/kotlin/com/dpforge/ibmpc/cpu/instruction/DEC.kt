package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.Register16

object DEC {

    fun reg16(opcode: Int, cpu: CPU) = with(cpu) {
        val reg = Register16.get(opcode - 0x48)
        registers.set(reg, alu.dec16(registers.get(reg)))
        registers.ip += 1
    }

    fun rmb(cpu: CPU) {
        cpu.rmb { op -> cpu.alu.dec8(op) }
    }

    fun rmw(cpu: CPU) {
        cpu.rmw { op -> cpu.alu.dec16(op) }
    }

}
