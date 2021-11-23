package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.timing.Timing
import com.dpforge.ibmpc.extensions.higherWord
import com.dpforge.ibmpc.extensions.lowerWord
import com.dpforge.ibmpc.extensions.signExtend16to32

object CWD {

    fun cwd(cpu: CPU): Int = with(cpu) {
        val extended = registers.ax.signExtend16to32()
        registers.dx = extended.higherWord
        registers.ax = extended.lowerWord
        registers.ip += 1
        Timing.cwd()
    }

}
