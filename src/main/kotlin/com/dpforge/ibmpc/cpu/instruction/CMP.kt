package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.AddressingMode
import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.Register16
import com.dpforge.ibmpc.cpu.Register8
import com.dpforge.ibmpc.extensions.signExtend8to16

object CMP {

    fun alIb(cpu: CPU) = with(cpu) {
        val b = memory.getByte(codeOffset + 1)
        alu.sub8(registers.get(Register8.AL), b)
        registers.ip += 2
    }

    fun axIw(cpu: CPU) = with(cpu) {
        val w = memory.getWord(codeOffset + 1)
        alu.sub16(registers.get(Register16.AX), w)
        registers.ip += 3
    }

    fun rmbIb(cpu: CPU) = with(cpu) {
        val addressingMode = AddressingMode.getForCurrentCodeOffset(cpu)
        val op1 = when (addressingMode) {
            is AddressingMode.Register -> registers.get(Register8.get(addressingMode.index))
            is AddressingMode.Memory -> memory.getByte(addressingMode.address)
        }
        val op2 = memory.getByte(codeOffset + addressingMode.byteCount)
        alu.sub8(op1, op2)
        registers.ip += addressingMode.byteCount + 1
    }

    fun rmbRb(cpu: CPU) = with(cpu) {
        val addressingMode = AddressingMode.getForCurrentCodeOffset(cpu)
        val op1 = when (addressingMode) {
            is AddressingMode.Register -> registers.get(Register8.get(addressingMode.index))
            is AddressingMode.Memory -> memory.getByte(addressingMode.address)
        }
        val op2 = registers.get(Register8.get(addressingMode.reg))
        alu.sub8(op1, op2)
        registers.ip += addressingMode.byteCount
    }

    fun rmwIb(cpu: CPU) = with(cpu) {
        val addressingMode = AddressingMode.getForCurrentCodeOffset(cpu)
        val op1 = when (addressingMode) {
            is AddressingMode.Register -> registers.get(Register16.get(addressingMode.index))
            is AddressingMode.Memory -> memory.getWord(addressingMode.address)
        }
        val op2 = memory.getByte(codeOffset + addressingMode.byteCount).signExtend8to16()
        alu.sub16(op1, op2)
        registers.ip += addressingMode.byteCount + 1
    }

    fun rmwRw(cpu: CPU) = with(cpu) {
        val addressingMode = AddressingMode.getForCurrentCodeOffset(cpu)
        val op1 = when (addressingMode) {
            is AddressingMode.Register -> registers.get(Register16.get(addressingMode.index))
            is AddressingMode.Memory -> memory.getWord(addressingMode.address)
        }
        val op2 = registers.get(Register16.get(addressingMode.reg))
        alu.sub16(op1, op2)
        registers.ip += addressingMode.byteCount
    }

    fun rbRmb(cpu: CPU) {
        cpu.rbRmb { op1, op2 ->
            cpu.alu.sub8(op1, op2)
            BinaryOperation.NO_RESULT
        }
    }

    fun rwRmw(cpu: CPU) {
        cpu.rwRmw { op1, op2 ->
            cpu.alu.sub16(op1, op2)
            BinaryOperation.NO_RESULT
        }
    }

    fun rmwIw(cpu: CPU) {
        cpu.rmwIw { op1, op2 ->
            cpu.alu.sub16(op1, op2)
            BinaryOperation.NO_RESULT
        }
    }
}