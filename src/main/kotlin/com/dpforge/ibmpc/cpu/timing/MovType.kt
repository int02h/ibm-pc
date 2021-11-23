package com.dpforge.ibmpc.cpu.timing

import com.dpforge.ibmpc.cpu.AddressingMode

sealed class MovType {
    object RegReg : MovType()
    class MemReg(val addressingMode: AddressingMode.Memory) : MovType()
    class RegMem(val addressingMode: AddressingMode.Memory) : MovType()
    class MemImm(val addressingMode: AddressingMode.Memory) : MovType()
    object RegImm : MovType()
    object AccMem : MovType()
    object MemAcc : MovType()
    object SRegReg : MovType()
    class SRegMem(val addressingMode: AddressingMode.Memory) : MovType()
    object RegSReg : MovType()
    class MemSReg(val addressingMode: AddressingMode.Memory) : MovType()
}