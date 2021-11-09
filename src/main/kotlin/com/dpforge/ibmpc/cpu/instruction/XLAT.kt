package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.SegmentRegister

object XLAT {

    fun xlat(cpu: CPU) = with(cpu) {
        val index = registers.al
        val address = makeAddress16(SegmentRegister.DS, registers.bx + index)
        registers.al = memory.getByte(address)
        registers.ip += 1
    }

}
