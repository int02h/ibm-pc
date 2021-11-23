package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.AddressingMode
import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.Register16
import com.dpforge.ibmpc.cpu.SegmentRegister
import com.dpforge.ibmpc.cpu.timing.Timing
import com.dpforge.ibmpc.cpu.timing.UnaryInstructionType

object PUSH {

    fun reg16(opcode: Int, cpu: CPU): Int = with(cpu) {
        val reg = Register16.get(opcode - 0x50)
        push16(registers.get(reg))
        registers.ip += 1
        Timing.push(UnaryInstructionType.Reg16)
    }

    fun segmentRegister(opcode: Int, cpu: CPU): Int = with(cpu) {
        val reg = SegmentRegister.get((opcode shr 3) and 0b11)
        push16(registers.get(reg))
        registers.ip += 1
        Timing.pushSeg()
    }

    fun rmw(cpu: CPU): Int = with(cpu) {
        val instructionType: UnaryInstructionType
        val addressingMode = AddressingMode.getForCurrentCodeOffset(cpu)
        push16(
            when (addressingMode) {
                is AddressingMode.Register -> {
                    instructionType = UnaryInstructionType.Reg16
                    registers.get(Register16.get(addressingMode.index))
                }
                is AddressingMode.Memory -> {
                    instructionType = UnaryInstructionType.Mem16(addressingMode)
                    memory.getWord(addressingMode.address)
                }
            }
        )
        registers.ip += addressingMode.byteCount
        Timing.push(instructionType)
    }

    fun flags(cpu: CPU): Int = with(cpu) {
        push16(registers.flags.value16)
        registers.ip += 1
        Timing.pushf()
    }

}