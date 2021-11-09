package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.AddressingMode
import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.Register16
import com.dpforge.ibmpc.cpu.Register8
import com.dpforge.ibmpc.cpu.SegmentRegister
import com.dpforge.ibmpc.extensions.exhaustive

object MOV {

    fun rmbRb(cpu: CPU) = with(cpu) {
        val addressingMode = AddressingMode.getForCurrentCodeOffset(cpu)
        val srcReg = Register8.get(addressingMode.reg)
        val srcValue = registers.get(srcReg)

        when (addressingMode) {
            is AddressingMode.Register -> registers.set(Register8.get(addressingMode.index), srcValue)
            is AddressingMode.Memory -> memory.setByte(addressingMode.address, srcValue)
        }.exhaustive

        registers.ip += addressingMode.byteCount
    }

    fun rmwRw(cpu: CPU) = with(cpu) {
        val addressingMode = AddressingMode.getForCurrentCodeOffset(cpu)
        val srcReg = Register16.get(addressingMode.reg)
        val srcValue = registers.get(srcReg)

        when (addressingMode) {
            is AddressingMode.Register -> registers.set(Register16.get(addressingMode.index), srcValue)
            is AddressingMode.Memory -> cpu.memory.setWord(addressingMode.address, srcValue)
        }.exhaustive

        registers.ip += addressingMode.byteCount
    }

    fun srRmw(cpu: CPU) = with(cpu) {
        val addressingMode = AddressingMode.getForCurrentCodeOffset(cpu)
        val src = when (addressingMode) {
            is AddressingMode.Register -> registers.get(Register16.get(addressingMode.index))
            is AddressingMode.Memory -> cpu.memory.getWord(addressingMode.address)
        }
        val dest = SegmentRegister.get(addressingMode.reg)
        registers.set(dest, src)
        registers.ip += addressingMode.byteCount
    }

    fun rbIb(opcode: Int, cpu: CPU) = with(cpu) {
        val reg = Register8.get(opcode - 0xB0)
        val b = memory.getByte(codeOffset + 1)
        registers.set(reg, b)
        registers.ip += 2
    }

    fun rwIw(opcode: Int, cpu: CPU) = with(cpu) {
        val reg = Register16.get(opcode - 0xB8)
        val w = memory.getWord(codeOffset + 1)
        registers.set(reg, w)
        registers.ip += 3
    }

    fun rmwSr(cpu: CPU) = with(cpu) {
        val addressingMode = AddressingMode.getForCurrentCodeOffset(cpu)

        val srcReg = SegmentRegister.get(addressingMode.reg)
        val srcValue = registers.get(srcReg)

        when (addressingMode) {
            is AddressingMode.Register -> registers.set(Register16.get(addressingMode.index), srcValue)
            is AddressingMode.Memory -> memory.setWord(addressingMode.address, srcValue)
        }.exhaustive

        registers.ip += addressingMode.byteCount
    }

    fun rbRmb(cpu: CPU): Unit = with(cpu) {
        val addressingMode = AddressingMode.getForCurrentCodeOffset(cpu)
        val src = when (addressingMode) {
            is AddressingMode.Register -> registers.get(Register8.get(addressingMode.index))
            is AddressingMode.Memory -> memory.getByte(addressingMode.address)
        }
        val dest = Register8.get(addressingMode.reg)
        registers.set(dest, src)
        registers.ip += addressingMode.byteCount
    }

    fun rwRmw(cpu: CPU) = with(cpu) {
        val addressingMode = AddressingMode.getForCurrentCodeOffset(cpu)
        val src = when (addressingMode) {
            is AddressingMode.Register -> registers.get(Register16.get(addressingMode.index))
            is AddressingMode.Memory -> memory.getWord(addressingMode.address)
        }
        val dest = Register16.get(addressingMode.reg)
        registers.set(dest, src)
        registers.ip += addressingMode.byteCount
    }

    fun mwAx(cpu: CPU) = with(cpu) {
        val offset = memory.getWord(codeOffset + 1)
        memory.setWord(makeAddress16(SegmentRegister.DS, offset), registers.ax)
        registers.ip += 3
    }

    fun axMw(cpu: CPU) = with(cpu) {
        val offset = memory.getWord(codeOffset + 1)
        registers.ax = memory.getWord(makeAddress16(SegmentRegister.DS, offset))
        registers.ip += 3
    }

    fun mbAl(cpu: CPU) = with(cpu) {
        val offset = memory.getWord(codeOffset + 1)
        memory.setByte(makeAddress16(SegmentRegister.DS, offset), registers.al)
        registers.ip += 3
    }

    fun alMb(cpu: CPU) = with(cpu) {
        val offset = memory.getWord(codeOffset + 1)
        registers.al = memory.getByte(makeAddress16(SegmentRegister.DS, offset))
        registers.ip += 3
    }

    fun rmwIw(cpu: CPU) = with(cpu) {
        val addressingMode = AddressingMode.getForCurrentCodeOffset(cpu)
        val srcValue = memory.getWord(codeOffset + addressingMode.byteCount)
        when (addressingMode) {
            is AddressingMode.Register -> registers.set(Register16.get(addressingMode.index), srcValue)
            is AddressingMode.Memory -> memory.setWord(addressingMode.address, srcValue)
        }.exhaustive
        registers.ip += addressingMode.byteCount + 2
    }

    fun rmbIb(cpu: CPU) = with(cpu) {
        val addressingMode = AddressingMode.getForCurrentCodeOffset(cpu)
        val srcValue = memory.getByte(codeOffset + addressingMode.byteCount)
        when (addressingMode) {
            is AddressingMode.Register -> registers.set(Register8.get(addressingMode.index), srcValue)
            is AddressingMode.Memory -> memory.setByte(addressingMode.address, srcValue)
        }.exhaustive
        registers.ip += addressingMode.byteCount + 1
    }
}