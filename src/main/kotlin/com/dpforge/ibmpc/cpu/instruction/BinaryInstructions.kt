package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.AddressingMode
import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.Register16
import com.dpforge.ibmpc.cpu.Register8
import com.dpforge.ibmpc.extensions.exhaustive
import com.dpforge.ibmpc.extensions.signExtend8to16

fun CPU.rmbIb(operation: BinaryOperation) {
    val addressingMode = AddressingMode.getForCurrentCodeOffset(this)
    val srcValue = memory.getByte(codeOffset + addressingMode.byteCount)

    when (addressingMode) {
        is AddressingMode.Register -> {
            val destReg = Register8.get(addressingMode.index)
            val destValue = registers.get(destReg)
            operation.perform(destValue, srcValue)?.let { registers.set(destReg, it) }
        }
        is AddressingMode.Memory -> {
            val destValue = memory.getByte(addressingMode.address)
            operation.perform(destValue, srcValue)?.let { memory.setByte(addressingMode.address, it) }
        }
    }.exhaustive

    registers.ip += addressingMode.byteCount + 1
}

fun CPU.rmbRb(operation: BinaryOperation) {
    val addressingMode = AddressingMode.getForCurrentCodeOffset(this)
    val srcReg = Register8.get(addressingMode.reg)
    val srcValue = registers.get(srcReg)

    when (addressingMode) {
        is AddressingMode.Register -> {
            val destReg = Register8.get(addressingMode.index)
            val destValue = registers.get(destReg)
            operation.perform(destValue, srcValue)?.let { registers.set(destReg, it) }
        }
        is AddressingMode.Memory -> {
            val destValue = memory.getByte(addressingMode.address)
            operation.perform(destValue, srcValue)?.let { memory.setByte(addressingMode.address, it) }
        }
    }.exhaustive

    registers.ip += addressingMode.byteCount
}

fun CPU.rbRmb(operation: BinaryOperation) {
    val addressingMode = AddressingMode.getForCurrentCodeOffset(this)
    val destReg = Register8.get(addressingMode.reg)
    val destValue = registers.get(destReg)

    val srcValue = when (addressingMode) {
        is AddressingMode.Register -> registers.get(Register8.get(addressingMode.index))
        is AddressingMode.Memory -> memory.getByte(addressingMode.address)
    }
    operation.perform(destValue, srcValue)?.let { registers.set(destReg, it) }
    registers.ip += addressingMode.byteCount
}

fun CPU.rmwRw(operation: BinaryOperation) {
    val addressingMode = AddressingMode.getForCurrentCodeOffset(this)
    val srcReg = Register16.get(addressingMode.reg)
    val srcValue = registers.get(srcReg)

    when (addressingMode) {
        is AddressingMode.Register -> {
            val destReg = Register16.get(addressingMode.index)
            val destValue = registers.get(destReg)
            operation.perform(destValue, srcValue)?.let { registers.set(destReg, it) }
        }
        is AddressingMode.Memory -> {
            val destValue = memory.getWord(addressingMode.address)
            operation.perform(destValue, srcValue)?.let { memory.setWord(addressingMode.address, it) }
        }
    }

    registers.ip += addressingMode.byteCount
}

fun CPU.rwRmw(operation: BinaryOperation) {
    val addressingMode = AddressingMode.getForCurrentCodeOffset(this)
    val srcValue = when (addressingMode) {
        is AddressingMode.Register -> registers.get(Register16.get(addressingMode.index))
        is AddressingMode.Memory -> memory.getWord(addressingMode.address)
    }
    val destReg = Register16.get(addressingMode.reg)
    val destValue = registers.get(destReg)

    operation.perform(destValue, srcValue)?.let { registers.set(destReg, it) }

    registers.ip += addressingMode.byteCount
}

fun CPU.rmwIb(operation: BinaryOperation) {
    val addressingMode = AddressingMode.getForCurrentCodeOffset(this)
    val srcValue = memory.getByte(codeOffset + addressingMode.byteCount).signExtend8to16()

    when (addressingMode) {
        is AddressingMode.Register -> {
            val destReg = Register16.get(addressingMode.index)
            val destValue = registers.get(destReg)
            operation.perform(destValue, srcValue)?.let { registers.set(destReg, it) }
        }
        is AddressingMode.Memory -> {
            val destValue = memory.getWord(addressingMode.address)
            operation.perform(destValue, srcValue)?.let { memory.setWord(addressingMode.address, it) }
        }
    }.exhaustive

    registers.ip += addressingMode.byteCount + 1
}

fun CPU.rmwIw(operation: BinaryOperation) {
    val addressingMode = AddressingMode.getForCurrentCodeOffset(this)
    val srcValue = memory.getWord(codeOffset + addressingMode.byteCount)

    when (addressingMode) {
        is AddressingMode.Register -> {
            val destReg = Register16.get(addressingMode.index)
            val destValue = registers.get(destReg)
            operation.perform(destValue, srcValue)?.let { registers.set(destReg, it) }
        }
        is AddressingMode.Memory -> {
            val destValue = memory.getWord(addressingMode.address)
            operation.perform(destValue, srcValue)?.let { memory.setWord(addressingMode.address, it) }
        }
    }.exhaustive

    registers.ip += addressingMode.byteCount + 2
}

fun interface BinaryOperation {
    fun perform(op1: Int, op2: Int): Int?

    companion object {
        val NO_RESULT: Int? = null
    }
}