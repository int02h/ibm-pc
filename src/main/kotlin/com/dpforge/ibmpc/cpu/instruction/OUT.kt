package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.timing.Timing

object OUT {

    fun alIb(cpu: CPU): Int = with(cpu) {
        val port = memory.getByte(codeOffset + 1)
        ports.write(port = port, value = registers.al)
        registers.ip += 2
        Timing.outImmAcc()
    }

    fun dxAl(cpu: CPU): Int = with(cpu) {
        ports.write(port = registers.dx, value = registers.al)
        registers.ip += 1
        Timing.outDxAcc()
    }

    fun dxAx(cpu: CPU): Int = with(cpu) {
        ports.write(port = registers.dx, value = registers.ax)
        registers.ip += 1
        Timing.outDxAcc()
    }
}