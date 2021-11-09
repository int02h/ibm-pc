package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.AddressingMode
import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.Register16

object LEA {

    fun lea(cpu: CPU) = with(cpu) {
        val addressingMode = AddressingMode.getForCurrentCodeOffset(cpu)
        val dest = Register16.get(addressingMode.reg)
        val address = when (addressingMode) {
            is AddressingMode.Register -> error("LEA cannot use register")
            is AddressingMode.Memory -> addressingMode.effectiveAddress
        }
        registers.set(dest, address)
        registers.ip += addressingMode.byteCount
    }

}
