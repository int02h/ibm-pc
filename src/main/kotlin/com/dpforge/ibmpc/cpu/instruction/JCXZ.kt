package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU

object JCXZ {

    fun short(cpu: CPU) {
        cpu.jumpShortIf { cx == 0 }
    }

}