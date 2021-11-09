package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.AddressingMode
import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.Register16
import com.dpforge.ibmpc.cpu.SegmentRegister

object PUSH {

    fun reg16(opcode: Int, cpu: CPU) = with(cpu) {
        val reg = Register16.get(opcode - 0x50)
        push16(registers.get(reg))
        registers.ip += 1
    }

    fun segmentRegister(opcode: Int, cpu: CPU) = with(cpu) {
        val reg = SegmentRegister.get((opcode shr 3) and 0b11)
        push16(registers.get(reg))
        registers.ip += 1
    }

    fun rmw(cpu: CPU) = with(cpu) {
        val addressingMode = AddressingMode.getForCurrentCodeOffset(cpu)
        push16(
            when (addressingMode) {
                is AddressingMode.Register -> registers.get(Register16.get(addressingMode.index))
                is AddressingMode.Memory -> memory.getWord(addressingMode.address)
            }
        )
        registers.ip += addressingMode.byteCount
    }

    fun flags(cpu: CPU) = with(cpu) {
        push16(registers.flags.value16)
        registers.ip += 1
    }

}