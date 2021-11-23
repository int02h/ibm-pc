package com.dpforge.ibmpc.cpu.timing

import com.dpforge.ibmpc.cpu.AddressingMode

sealed class CallType {
    object Near : CallType()
    object Reg : CallType()
    class Mem16(val addressingMode: AddressingMode.Memory) : CallType()
    object Far : CallType()
    class Mem32(val addressingMode: AddressingMode.Memory) : CallType()
}