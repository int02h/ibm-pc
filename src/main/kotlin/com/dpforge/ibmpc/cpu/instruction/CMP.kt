package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.AddressingMode
import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.Register16
import com.dpforge.ibmpc.cpu.Register8
import com.dpforge.ibmpc.cpu.timing.Timing
import com.dpforge.ibmpc.cpu.timing.BinaryInstructionType
import com.dpforge.ibmpc.extensions.signExtend8to16

object CMP {

    fun alIb(cpu: CPU): Int = with(cpu) {
        val b = memory.getByte(codeOffset + 1)
        alu.sub8(registers.get(Register8.AL), b)
        registers.ip += 2
        Timing.cmp(BinaryInstructionType.AccImm)
    }

    fun axIw(cpu: CPU): Int = with(cpu) {
        val w = memory.getWord(codeOffset + 1)
        alu.sub16(registers.get(Register16.AX), w)
        registers.ip += 3
        Timing.cmp(BinaryInstructionType.AccImm)
    }

    fun rmbIb(cpu: CPU): Int = with(cpu) {
        val instructionType: BinaryInstructionType
        val addressingMode = AddressingMode.getForCurrentCodeOffset(cpu)
        val op1 = when (addressingMode) {
            is AddressingMode.Register -> {
                instructionType = BinaryInstructionType.RegImm
                registers.get(Register8.get(addressingMode.index))
            }
            is AddressingMode.Memory -> {
                instructionType = BinaryInstructionType.MemImm(addressingMode)
                memory.getByte(addressingMode.address)
            }
        }
        val op2 = memory.getByte(codeOffset + addressingMode.byteCount)
        alu.sub8(op1, op2)
        registers.ip += addressingMode.byteCount + 1
        Timing.cmp(instructionType)
    }

    fun rmbRb(cpu: CPU): Int = with(cpu) {
        val instructionType: BinaryInstructionType
        val addressingMode = AddressingMode.getForCurrentCodeOffset(cpu)
        val op1 = when (addressingMode) {
            is AddressingMode.Register -> {
                instructionType = BinaryInstructionType.RegReg
                registers.get(Register8.get(addressingMode.index))
            }
            is AddressingMode.Memory -> {
                instructionType = BinaryInstructionType.MemReg(addressingMode)
                memory.getByte(addressingMode.address)
            }
        }
        val op2 = registers.get(Register8.get(addressingMode.reg))
        alu.sub8(op1, op2)
        registers.ip += addressingMode.byteCount
        Timing.cmp(instructionType)
    }

    fun rmwIb(cpu: CPU): Int = with(cpu) {
        val instructionType: BinaryInstructionType
        val addressingMode = AddressingMode.getForCurrentCodeOffset(cpu)
        val op1 = when (addressingMode) {
            is AddressingMode.Register -> {
                instructionType = BinaryInstructionType.RegImm
                registers.get(Register16.get(addressingMode.index))
            }
            is AddressingMode.Memory -> {
                instructionType = BinaryInstructionType.MemImm(addressingMode)
                memory.getWord(addressingMode.address)
            }
        }
        val op2 = memory.getByte(codeOffset + addressingMode.byteCount).signExtend8to16()
        alu.sub16(op1, op2)
        registers.ip += addressingMode.byteCount + 1
        Timing.cmp(instructionType)
    }

    fun rmwRw(cpu: CPU): Int = with(cpu) {
        val instructionType: BinaryInstructionType
        val addressingMode = AddressingMode.getForCurrentCodeOffset(cpu)
        val op1 = when (addressingMode) {
            is AddressingMode.Register -> {
                instructionType = BinaryInstructionType.RegReg
                registers.get(Register16.get(addressingMode.index))
            }
            is AddressingMode.Memory -> {
                instructionType = BinaryInstructionType.MemReg(addressingMode)
                memory.getWord(addressingMode.address)
            }
        }
        val op2 = registers.get(Register16.get(addressingMode.reg))
        alu.sub16(op1, op2)
        registers.ip += addressingMode.byteCount
        Timing.cmp(instructionType)
    }

    fun rbRmb(cpu: CPU): Int = Timing.cmp(
        cpu.rbRmb { op1, op2 ->
            cpu.alu.sub8(op1, op2)
            BinaryOperation.NO_RESULT
        }
    )

    fun rwRmw(cpu: CPU): Int = Timing.cmp(
        cpu.rwRmw { op1, op2 ->
            cpu.alu.sub16(op1, op2)
            BinaryOperation.NO_RESULT
        }
    )

    fun rmwIw(cpu: CPU): Int = Timing.cmp(
        cpu.rmwIw { op1, op2 ->
            cpu.alu.sub16(op1, op2)
            BinaryOperation.NO_RESULT
        }
    )
}