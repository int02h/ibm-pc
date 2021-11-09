package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.AddressingMode
import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.Register16
import com.dpforge.ibmpc.cpu.Register8
import com.dpforge.ibmpc.extensions.exhaustive

object XCHG {

    fun axRw(opcode: Int, cpu: CPU) = with(cpu) {
        val reg = Register16.get(opcode - 0x90)
        val tmp = registers.get(reg)
        registers.set(reg, registers.ax)
        registers.ax = tmp
        registers.ip += 1
    }

    fun rwRmw(cpu: CPU) = with(cpu) {
        val addressingMode = AddressingMode.getForCurrentCodeOffset(cpu)

        val destReg = Register16.get(addressingMode.reg)
        val destValue = registers.get(destReg)

        when (addressingMode) {
            is AddressingMode.Register -> {
                val srcReg = Register16.get(addressingMode.index)
                val tmp = registers.get(srcReg)
                registers.set(srcReg, destValue)
                registers.set(destReg, tmp)
            }
            is AddressingMode.Memory -> {
                val tmp = memory.getWord(addressingMode.address)
                memory.setWord(addressingMode.address, destValue)
                registers.set(destReg, tmp)
            }
        }.exhaustive

        registers.ip += addressingMode.byteCount
    }

    fun rbRmb(cpu: CPU) = with(cpu){
        val addressingMode = AddressingMode.getForCurrentCodeOffset(cpu)

        val destReg = Register8.get(addressingMode.reg)
        val destValue = registers.get(destReg)

        when (addressingMode) {
            is AddressingMode.Register -> {
                val srcReg = Register8.get(addressingMode.index)
                val tmp = registers.get(srcReg)
                registers.set(srcReg, destValue)
                registers.set(destReg, tmp)
            }
            is AddressingMode.Memory -> {
                val tmp = memory.getByte(addressingMode.address)
                memory.setByte(addressingMode.address, destValue)
                registers.set(destReg, tmp)
            }
        }.exhaustive

        registers.ip += addressingMode.byteCount
    }

}
