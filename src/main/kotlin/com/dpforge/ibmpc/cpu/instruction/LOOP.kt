package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU

object LOOP {

    fun short(cpu: CPU) {
        cpu.jumpShortIf {
            cx -= 1
            cx != 0
        }
    }

}