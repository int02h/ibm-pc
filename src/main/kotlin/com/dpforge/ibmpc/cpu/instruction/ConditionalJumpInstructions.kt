package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.Registers

fun CPU.jumpShortIf(predicate: Registers.() -> Boolean) {
    val relativeDisplacement = memory.getByte(codeOffset + 1).toByte()
    registers.ip += 2 // for instruction itself
    if (predicate(registers)) {
        registers.ip += relativeDisplacement
    }
}