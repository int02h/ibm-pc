package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.timing.Timing
import com.dpforge.ibmpc.extensions.signExtend8to16

object CBW {

    fun cbw(cpu: CPU):Int = with(cpu) {
        registers.ax = registers.al.signExtend8to16()
        registers.ip += 1
        Timing.cbw()
    }

}
