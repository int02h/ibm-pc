package com.dpforge.ibmpc.cpu.timing

import com.dpforge.ibmpc.cpu.AddressingMode

sealed class XCHGType {
    object RegReg : XCHGType()
    class RegMem(val addressingMode: AddressingMode.Memory) : XCHGType()
    class MemReg(val addressingMode: AddressingMode.Memory) : XCHGType()
    object AccReg : XCHGType()
}