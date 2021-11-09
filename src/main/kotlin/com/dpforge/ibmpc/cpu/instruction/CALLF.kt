package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.AddressingMode
import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.extensions.toHex

object CALLF {

    fun mww(cpu: CPU) = with(cpu) {
        val addressingMode = AddressingMode.getForCurrentCodeOffset(cpu)

        registers.ip += addressingMode.byteCount
        push16(registers.cs)
        push16(registers.ip)

        val address = when (addressingMode) {
            is AddressingMode.Register -> error("Register addressing mode is invalid for JMPF")
            is AddressingMode.Memory -> addressingMode.address
        }
        registers.ip = memory.getWord(address)
        registers.cs = memory.getWord(address + 2)

        if (registers.ip == 0 && registers.cs == 0) {
            error("Bad far call. Address ${address.toHex()}")
        }
    }

}
