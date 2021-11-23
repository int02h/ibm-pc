package com.dpforge.ibmpc.cpu.timing

import com.dpforge.ibmpc.cpu.AddressingMode

sealed class BitShiftType {
    object Reg1 : BitShiftType()
    class Mem1(val addressingMode: AddressingMode.Memory) : BitShiftType()
    class RegCL(val count: Int) : BitShiftType()
    class MemCL(val count: Int, val addressingMode: AddressingMode.Memory) : BitShiftType()
}

fun UnaryInstructionType.toBitShiftType1() = when (this) {
    is UnaryInstructionType.Reg8 -> BitShiftType.Reg1
    is UnaryInstructionType.Reg16 -> BitShiftType.Reg1
    is UnaryInstructionType.Mem8 -> BitShiftType.Mem1(addressingMode)
    is UnaryInstructionType.Mem16 -> BitShiftType.Mem1(addressingMode)
}

fun UnaryInstructionType.toBitShiftTypeCL(count: Int) = when (this) {
    is UnaryInstructionType.Reg8 -> BitShiftType.RegCL(count)
    is UnaryInstructionType.Reg16 -> BitShiftType.RegCL(count)
    is UnaryInstructionType.Mem8 -> BitShiftType.MemCL(count, addressingMode)
    is UnaryInstructionType.Mem16 -> BitShiftType.MemCL(count, addressingMode)
}