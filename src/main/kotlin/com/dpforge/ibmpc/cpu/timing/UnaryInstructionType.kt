package com.dpforge.ibmpc.cpu.timing

import com.dpforge.ibmpc.cpu.AddressingMode

sealed class UnaryInstructionType {
    object Reg8 : UnaryInstructionType()
    object Reg16 : UnaryInstructionType()
    class Mem8(val addressingMode: AddressingMode.Memory) : UnaryInstructionType()
    class Mem16(val addressingMode: AddressingMode.Memory) : UnaryInstructionType()
}