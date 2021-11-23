package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.Registers
import com.dpforge.ibmpc.cpu.timing.Timing

fun CPU.jumpShortIf(timing: Timing.ConditionalTiming, predicate: Registers.() -> Boolean): Int {
    val relativeDisplacement = memory.getByte(codeOffset + 1).toByte()
    registers.ip += 2 // for instruction itself
    return if (predicate(registers)) {
        registers.ip += relativeDisplacement
        timing.jumpCycle
    } else {
        timing.noJumpCycles
    }
}