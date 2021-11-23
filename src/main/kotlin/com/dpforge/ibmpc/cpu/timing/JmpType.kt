package com.dpforge.ibmpc.cpu.timing

import com.dpforge.ibmpc.cpu.AddressingMode

sealed class JmpType {
    object Short : JmpType()
    object Near : JmpType()
    object Far : JmpType()
    object Reg16 : JmpType()
    class Mem16(val addressingMode: AddressingMode.Memory) : JmpType()
    class Mem32(val addressingMode: AddressingMode.Memory) : JmpType()
}