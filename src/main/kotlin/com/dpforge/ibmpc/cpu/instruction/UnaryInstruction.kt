package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.AddressingMode
import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.Register16
import com.dpforge.ibmpc.cpu.Register8
import com.dpforge.ibmpc.cpu.timing.UnaryInstructionType

fun CPU.rmb(operation: UnaryOperation): UnaryInstructionType {
    val instructionType: UnaryInstructionType
    val addressingMode = AddressingMode.getForCurrentCodeOffset(this)
    when (addressingMode) {
        is AddressingMode.Register -> {
            instructionType = UnaryInstructionType.Reg8
            val reg = Register8.get(addressingMode.index)
            operation.perform(registers.get(reg))?.let { registers.set(reg, it) }
        }
        is AddressingMode.Memory -> {
            instructionType = UnaryInstructionType.Mem8(addressingMode)
            operation.perform(memory.getByte(addressingMode.address))
                ?.let { memory.setByte(addressingMode.address, it) }
        }
    }
    registers.ip += addressingMode.byteCount
    return instructionType
}

fun CPU.rmw(operation: UnaryOperation): UnaryInstructionType {
    val instructionType: UnaryInstructionType
    val addressingMode = AddressingMode.getForCurrentCodeOffset(this)
    when (addressingMode) {
        is AddressingMode.Register -> {
            instructionType = UnaryInstructionType.Reg16
            val reg = Register16.get(addressingMode.index)
            operation.perform(registers.get(reg))?.let { registers.set(reg, it) }
        }
        is AddressingMode.Memory -> {
            instructionType = UnaryInstructionType.Mem16(addressingMode)
            operation.perform(memory.getWord(addressingMode.address))
                ?.let { memory.setWord(addressingMode.address, it) }
        }
    }
    registers.ip += addressingMode.byteCount
    return instructionType
}

fun interface UnaryOperation {
    fun perform(op: Int): Int?

    companion object {
        val NO_RESULT: Int? = null
    }
}