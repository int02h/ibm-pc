package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.timing.Timing

object IN {

    fun alIb(cpu: CPU): Int = with(cpu) {
        val port = memory.getByte(codeOffset + 1)
        registers.al = ports.readByte(port)
        registers.ip += 2
        Timing.inAccImm()
    }

    fun axIb(cpu: CPU): Int = with(cpu) {
        val port = memory.getByte(codeOffset + 1)
        registers.ax = ports.readWord(port)
        registers.ip += 2
        Timing.inAccImm()
    }

    fun alDX(cpu: CPU): Int = with(cpu) {
        registers.al = ports.readByte(registers.dx)
        registers.ip += 1
        Timing.inAccDx()
    }

    fun axDX(cpu: CPU): Int = with(cpu) {
        registers.ax = ports.readWord(registers.dx)
        registers.ip += 1
        Timing.inAccDx()
    }

}