package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.AddressingMode
import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.Register16
import com.dpforge.ibmpc.cpu.Register8
import com.dpforge.ibmpc.cpu.SegmentRegister
import com.dpforge.ibmpc.cpu.timing.Timing
import com.dpforge.ibmpc.cpu.timing.MovType
import com.dpforge.ibmpc.extensions.exhaustive

object MOV {

    fun rmbRb(cpu: CPU): Int = with(cpu) {
        val instructionType: MovType
        val addressingMode = AddressingMode.getForCurrentCodeOffset(cpu)
        val srcReg = Register8.get(addressingMode.reg)
        val srcValue = registers.get(srcReg)

        when (addressingMode) {
            is AddressingMode.Register -> {
                instructionType = MovType.RegReg
                registers.set(Register8.get(addressingMode.index), srcValue)
            }
            is AddressingMode.Memory -> {
                instructionType = MovType.MemReg(addressingMode)
                memory.setByte(addressingMode.address, srcValue)
            }
        }.exhaustive

        registers.ip += addressingMode.byteCount
        Timing.mov(instructionType)
    }

    fun rmwRw(cpu: CPU): Int = with(cpu) {
        val instructionType: MovType
        val addressingMode = AddressingMode.getForCurrentCodeOffset(cpu)
        val srcReg = Register16.get(addressingMode.reg)
        val srcValue = registers.get(srcReg)

        when (addressingMode) {
            is AddressingMode.Register -> {
                instructionType = MovType.RegReg
                registers.set(Register16.get(addressingMode.index), srcValue)
            }
            is AddressingMode.Memory -> {
                instructionType = MovType.MemReg(addressingMode)
                cpu.memory.setWord(addressingMode.address, srcValue)
            }
        }.exhaustive

        registers.ip += addressingMode.byteCount
        Timing.mov(instructionType)
    }

    fun srRmw(cpu: CPU): Int = with(cpu) {
        val instructionType: MovType
        val addressingMode = AddressingMode.getForCurrentCodeOffset(cpu)
        val src = when (addressingMode) {
            is AddressingMode.Register -> {
                instructionType = MovType.SRegReg
                registers.get(Register16.get(addressingMode.index))
            }
            is AddressingMode.Memory -> {
                instructionType = MovType.SRegMem(addressingMode)
                cpu.memory.getWord(addressingMode.address)
            }
        }
        val dest = SegmentRegister.get(addressingMode.reg)
        registers.set(dest, src)
        registers.ip += addressingMode.byteCount
        Timing.mov(instructionType)
    }

    fun rbIb(opcode: Int, cpu: CPU): Int = with(cpu) {
        val reg = Register8.get(opcode - 0xB0)
        val b = memory.getByte(codeOffset + 1)
        registers.set(reg, b)
        registers.ip += 2
        Timing.mov(MovType.RegImm)
    }

    fun rwIw(opcode: Int, cpu: CPU): Int = with(cpu) {
        val reg = Register16.get(opcode - 0xB8)
        val w = memory.getWord(codeOffset + 1)
        registers.set(reg, w)
        registers.ip += 3
        Timing.mov(MovType.RegImm)
    }

    fun rmwSr(cpu: CPU): Int = with(cpu) {
        val instructionType: MovType
        val addressingMode = AddressingMode.getForCurrentCodeOffset(cpu)

        val srcReg = SegmentRegister.get(addressingMode.reg)
        val srcValue = registers.get(srcReg)

        when (addressingMode) {
            is AddressingMode.Register -> {
                instructionType = MovType.RegSReg
                registers.set(Register16.get(addressingMode.index), srcValue)
            }
            is AddressingMode.Memory -> {
                instructionType = MovType.MemSReg(addressingMode)
                memory.setWord(addressingMode.address, srcValue)
            }
        }.exhaustive

        registers.ip += addressingMode.byteCount
        Timing.mov(instructionType)
    }

    fun rbRmb(cpu: CPU): Int = with(cpu) {
        val instructionType: MovType
        val addressingMode = AddressingMode.getForCurrentCodeOffset(cpu)
        val src = when (addressingMode) {
            is AddressingMode.Register -> {
                instructionType = MovType.RegReg
                registers.get(Register8.get(addressingMode.index))
            }
            is AddressingMode.Memory -> {
                instructionType = MovType.RegMem(addressingMode)
                memory.getByte(addressingMode.address)
            }
        }
        val dest = Register8.get(addressingMode.reg)
        registers.set(dest, src)
        registers.ip += addressingMode.byteCount
        Timing.mov(instructionType)
    }

    fun rwRmw(cpu: CPU): Int = with(cpu) {
        val instructionType: MovType
        val addressingMode = AddressingMode.getForCurrentCodeOffset(cpu)
        val src = when (addressingMode) {
            is AddressingMode.Register -> {
                instructionType = MovType.RegReg
                registers.get(Register16.get(addressingMode.index))
            }
            is AddressingMode.Memory -> {
                instructionType = MovType.RegMem(addressingMode)
                memory.getWord(addressingMode.address)
            }
        }
        val dest = Register16.get(addressingMode.reg)
        registers.set(dest, src)
        registers.ip += addressingMode.byteCount
        Timing.mov(instructionType)
    }

    fun mwAx(cpu: CPU): Int = with(cpu) {
        val offset = memory.getWord(codeOffset + 1)
        memory.setWord(makeAddress16(SegmentRegister.DS, offset), registers.ax)
        registers.ip += 3
        Timing.mov(MovType.MemAcc)
    }

    fun axMw(cpu: CPU): Int = with(cpu) {
        val offset = memory.getWord(codeOffset + 1)
        registers.ax = memory.getWord(makeAddress16(SegmentRegister.DS, offset))
        registers.ip += 3
        Timing.mov(MovType.AccMem)
    }

    fun mbAl(cpu: CPU): Int = with(cpu) {
        val offset = memory.getWord(codeOffset + 1)
        memory.setByte(makeAddress16(SegmentRegister.DS, offset), registers.al)
        registers.ip += 3
        Timing.mov(MovType.MemAcc)
    }

    fun alMb(cpu: CPU): Int = with(cpu) {
        val offset = memory.getWord(codeOffset + 1)
        registers.al = memory.getByte(makeAddress16(SegmentRegister.DS, offset))
        registers.ip += 3
        Timing.mov(MovType.AccMem)
    }

    fun rmwIw(cpu: CPU): Int = with(cpu) {
        val instructionType: MovType
        val addressingMode = AddressingMode.getForCurrentCodeOffset(cpu)
        val srcValue = memory.getWord(codeOffset + addressingMode.byteCount)
        when (addressingMode) {
            is AddressingMode.Register -> {
                instructionType = MovType.RegImm
                registers.set(Register16.get(addressingMode.index), srcValue)
            }
            is AddressingMode.Memory -> {
                instructionType = MovType.MemImm(addressingMode)
                memory.setWord(addressingMode.address, srcValue)
            }
        }.exhaustive
        registers.ip += addressingMode.byteCount + 2
        Timing.mov(instructionType)
    }

    fun rmbIb(cpu: CPU): Int = with(cpu) {
        val instructionType: MovType
        val addressingMode = AddressingMode.getForCurrentCodeOffset(cpu)
        val srcValue = memory.getByte(codeOffset + addressingMode.byteCount)
        when (addressingMode) {
            is AddressingMode.Register -> {
                instructionType = MovType.RegImm
                registers.set(Register8.get(addressingMode.index), srcValue)
            }
            is AddressingMode.Memory -> {
                instructionType = MovType.MemImm(addressingMode)
                memory.setByte(addressingMode.address, srcValue)
            }
        }.exhaustive
        registers.ip += addressingMode.byteCount + 1
        Timing.mov(instructionType)
    }
}