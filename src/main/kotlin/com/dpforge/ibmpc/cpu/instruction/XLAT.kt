package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.SegmentRegister
import com.dpforge.ibmpc.cpu.timing.Timing

object XLAT {

    fun xlat(cpu: CPU): Int = with(cpu) {
        val index = registers.al
        val address = makeAddress16(SegmentRegister.DS, registers.bx + index)
        registers.al = memory.getByte(address)
        registers.ip += 1
        Timing.xlat()
    }

}
