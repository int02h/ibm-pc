package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU

object IN {

    fun alIb(cpu: CPU) = with(cpu) {
        val port = memory.getByte(codeOffset + 1)
        registers.al = ports.readByte(port)
        registers.ip += 2
    }

    fun axIb(cpu: CPU) = with(cpu) {
        val port = memory.getByte(codeOffset + 1)
        registers.ax = ports.readWord(port)
        registers.ip += 2
    }

    fun alDX(cpu: CPU) = with(cpu) {
        registers.al = ports.readByte(registers.dx)
        registers.ip += 1
    }

    fun axDX(cpu: CPU) = with(cpu) {
        registers.ax = ports.readWord(registers.dx)
        registers.ip += 1
    }

}