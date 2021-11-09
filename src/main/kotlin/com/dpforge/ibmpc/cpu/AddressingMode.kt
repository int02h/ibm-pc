package com.dpforge.ibmpc.cpu

import com.dpforge.ibmpc.utils.impossible

sealed class AddressingMode(val reg: Int, val byteCount: Int) {

    class Register(
        val index: Int,
        reg: Int,
        byteCount: Int
    ) : AddressingMode(reg, byteCount)

    class Memory(
        val address: Int,
        val effectiveAddress: Int,
        reg: Int,
        byteCount: Int
    ) : AddressingMode(reg, byteCount)

    companion object {
        fun getForCurrentCodeOffset(cpu: CPU): AddressingMode {
            val modRmByte = cpu.memory.getByte(cpu.codeOffset + 1)
            val mod = modRmByte shr 6
            val rm = modRmByte and 0b111
            val reg = (modRmByte shr 3) and 0b111
            return when (mod) {
                0b00 -> when (rm) {
                    0b110 -> getDisplacementOnly(cpu, reg)
                    else -> getRegisterIndirect(cpu, rm, reg)
                }
                0b01 -> getRwIbIndirect(cpu = cpu, rm = rm, reg = reg)
                0b10 -> getRwIwIndirect(cpu = cpu, rm = rm, reg = reg)
                0b11 -> Register(rm, reg, 2)
                else -> error("Illegal mod $mod")
            }
        }

        private fun getRegisterIndirect(cpu: CPU, rm: Int, reg: Int): AddressingMode = when (rm) {
            0 -> {
                val effectiveAddress = cpu.registers.get(Register16.BX) + cpu.registers.get(Register16.SI)
                createMemoryMode(SegmentRegister.DS, effectiveAddress, reg, byteCount = 2, cpu)
            }
            1 -> {
                val effectiveAddress = cpu.registers.get(Register16.BX) + cpu.registers.get(Register16.DI)
                createMemoryMode(SegmentRegister.DS, effectiveAddress, reg, byteCount = 2, cpu)
            }
            2 -> {
                val effectiveAddress = cpu.registers.get(Register16.BP) + cpu.registers.get(Register16.SI)
                createMemoryMode(SegmentRegister.SS, effectiveAddress, reg, byteCount = 2, cpu)
            }
            3 -> {
                val effectiveAddress = cpu.registers.get(Register16.BP) + cpu.registers.get(Register16.DI)
                createMemoryMode(SegmentRegister.SS, effectiveAddress, reg, byteCount = 2, cpu)
            }
            4 -> {
                val effectiveAddress = cpu.registers.get(Register16.SI)
                createMemoryMode(SegmentRegister.DS, effectiveAddress, reg, byteCount = 2, cpu)
            }
            5 -> {
                val effectiveAddress = cpu.registers.get(Register16.DI)
                createMemoryMode(SegmentRegister.DS, effectiveAddress, reg, byteCount = 2, cpu)
            }
            7 -> {
                val effectiveAddress = cpu.registers.get(Register16.BX)
                createMemoryMode(SegmentRegister.DS, effectiveAddress, reg, byteCount = 2, cpu)
            }
            else -> impossible()
        }

        private fun getDisplacementOnly(cpu: CPU, reg: Int): AddressingMode = with(cpu) {
            val effectiveAddress = memory.getWord(codeOffset + 2)
            return createMemoryMode(SegmentRegister.DS, effectiveAddress, reg, byteCount = 4, cpu)
        }

        private fun getRwIbIndirect(cpu: CPU, rm: Int, reg: Int) = when (rm) {
            0 -> {
                val b = cpu.memory.getByte(cpu.codeOffset + 2).toByte()
                val effectiveAddress = cpu.registers.get(Register16.BX) + cpu.registers.get(Register16.SI) + b
                createMemoryMode(SegmentRegister.DS, effectiveAddress, reg, byteCount = 3, cpu)
            }
            2 -> {
                val b = cpu.memory.getByte(cpu.codeOffset + 2).toByte()
                val effectiveAddress = cpu.registers.get(Register16.BP) + cpu.registers.get(Register16.SI) + b
                createMemoryMode(SegmentRegister.SS, effectiveAddress, reg, byteCount = 3, cpu)
            }
            3 -> {
                val b = cpu.memory.getByte(cpu.codeOffset + 2).toByte()
                val effectiveAddress = cpu.registers.get(Register16.BP) + cpu.registers.get(Register16.DI) + b
                createMemoryMode(SegmentRegister.SS, effectiveAddress, reg, byteCount = 3, cpu)
            }
            4 -> {
                val b = cpu.memory.getByte(cpu.codeOffset + 2).toByte()
                val effectiveAddress = cpu.registers.get(Register16.SI) + b
                createMemoryMode(SegmentRegister.DS, effectiveAddress, reg, byteCount = 3, cpu)
            }
            5 -> {
                val b = cpu.memory.getByte(cpu.codeOffset + 2).toByte()
                val effectiveAddress = cpu.registers.get(Register16.DI) + b
                createMemoryMode(SegmentRegister.DS, effectiveAddress, reg, byteCount = 3, cpu)
            }
            6 -> {
                val b = cpu.memory.getByte(cpu.codeOffset + 2).toByte()
                val effectiveAddress = cpu.registers.get(Register16.BP) + b
                createMemoryMode(SegmentRegister.SS, effectiveAddress, reg, byteCount = 3, cpu)
            }
            7 -> {
                val b = cpu.memory.getByte(cpu.codeOffset + 2).toByte()
                val effectiveAddress = cpu.registers.get(Register16.BX) + b
                createMemoryMode(SegmentRegister.DS, effectiveAddress, reg, byteCount = 3, cpu)
            }
            else -> TODO("$rm")
        }

        private fun getRwIwIndirect(cpu: CPU, rm: Int, reg: Int): AddressingMode = when (rm) {
            3 -> {
                val w = cpu.memory.getWord(cpu.codeOffset + 2).toShort()
                val effectiveAddress = cpu.registers.get(Register16.BP) + cpu.registers.get(Register16.DI) + w
                createMemoryMode(SegmentRegister.SS, effectiveAddress, reg, byteCount = 4, cpu)
            }
            4 -> {
                val w = cpu.memory.getWord(cpu.codeOffset + 2).toShort()
                val effectiveAddress = cpu.registers.get(Register16.SI) + w
                createMemoryMode(SegmentRegister.DS, effectiveAddress, reg, byteCount = 4, cpu)
            }
            5 -> {
                val w = cpu.memory.getWord(cpu.codeOffset + 2).toShort()
                val effectiveAddress = cpu.registers.get(Register16.DI) + w
                createMemoryMode(SegmentRegister.DS, effectiveAddress, reg, byteCount = 4, cpu)
            }
            6 -> {
                val w = cpu.memory.getWord(cpu.codeOffset + 2).toShort()
                val effectiveAddress = cpu.registers.get(Register16.BP) + w
                createMemoryMode(SegmentRegister.SS, effectiveAddress, reg, byteCount = 4, cpu)
            }
            7 -> {
                val w = cpu.memory.getWord(cpu.codeOffset + 2).toShort()
                val effectiveAddress = cpu.registers.get(Register16.BX) + w
                createMemoryMode(SegmentRegister.DS, effectiveAddress, reg, byteCount = 4, cpu)
            }
            else -> TODO("$rm")
        }

        private fun createMemoryMode(
            segmentRegister: SegmentRegister,
            effectiveAddress: Int,
            reg: Int,
            byteCount: Int,
            cpu: CPU
        ) = Memory(
            address = cpu.makeAddress16(segmentRegister, effectiveAddress),
            effectiveAddress = effectiveAddress,
            reg = reg,
            byteCount = byteCount
        )

    }

}