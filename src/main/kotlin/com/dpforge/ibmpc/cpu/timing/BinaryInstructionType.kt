package com.dpforge.ibmpc.cpu.timing

import com.dpforge.ibmpc.cpu.AddressingMode

sealed class BinaryInstructionType {
    object RegReg : BinaryInstructionType()
    class MemReg(val addressingMode: AddressingMode.Memory) : BinaryInstructionType()
    class RegMem(val addressingMode: AddressingMode.Memory) : BinaryInstructionType()
    object RegImm : BinaryInstructionType()
    class MemImm(val addressingMode: AddressingMode.Memory) : BinaryInstructionType()
    object AccImm : BinaryInstructionType()
}