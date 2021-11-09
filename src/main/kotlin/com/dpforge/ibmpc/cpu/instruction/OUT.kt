package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU

object OUT {

    fun alIb(cpu: CPU) = with(cpu) {
        val port = memory.getByte(codeOffset + 1)
        ports.write(port = port, value = registers.al)
        registers.ip += 2
    }

    fun dxAl(cpu: CPU) = with(cpu){
        ports.write(port = registers.dx, value = registers.al)
        registers.ip += 1
    }

    fun dxAx(cpu: CPU) = with(cpu){
        ports.write(port = registers.dx, value = registers.ax)
        registers.ip += 1
    }
}