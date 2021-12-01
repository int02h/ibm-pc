package com.dpforge.ibmpc.cpu

import com.dpforge.ibmpc.cpu.timing.Timing.AddressingModeTiming
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
        val clocks: Int,
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
                else -> impossible()
            }
        }

        private fun getRegisterIndirect(cpu: CPU, rm: Int, reg: Int): AddressingMode = when (rm) {
            0 -> cpu.createRegisterIndirectMemoryMode(
                segmentRegister = SegmentRegister.DS,
                effectiveAddress = cpu.registers.get(Register16.BX) + cpu.registers.get(Register16.SI),
                reg = reg,
                timing = AddressingModeTiming.BX_SI
            )
            1 -> cpu.createRegisterIndirectMemoryMode(
                segmentRegister = SegmentRegister.DS,
                effectiveAddress = cpu.registers.get(Register16.BX) + cpu.registers.get(Register16.DI),
                reg = reg,
                timing = AddressingModeTiming.BX_DI
            )
            2 -> cpu.createRegisterIndirectMemoryMode(
                segmentRegister = SegmentRegister.SS,
                effectiveAddress = cpu.registers.get(Register16.BP) + cpu.registers.get(Register16.SI),
                reg = reg,
                timing = AddressingModeTiming.BP_SI
            )
            3 -> cpu.createRegisterIndirectMemoryMode(
                segmentRegister = SegmentRegister.SS,
                effectiveAddress = cpu.registers.get(Register16.BP) + cpu.registers.get(Register16.DI),
                reg = reg,
                timing = AddressingModeTiming.BP_DI
            )
            4 -> cpu.createRegisterIndirectMemoryMode(
                segmentRegister = SegmentRegister.DS,
                effectiveAddress = cpu.registers.get(Register16.SI),
                reg = reg,
                timing = AddressingModeTiming.BASE_OR_INDEX
            )
            5 -> cpu.createRegisterIndirectMemoryMode(
                segmentRegister = SegmentRegister.DS,
                effectiveAddress = cpu.registers.get(Register16.DI),
                reg = reg,
                timing = AddressingModeTiming.BASE_OR_INDEX
            )
            7 -> cpu.createRegisterIndirectMemoryMode(
                segmentRegister = SegmentRegister.DS,
                effectiveAddress = cpu.registers.get(Register16.BX),
                reg = reg,
                timing = AddressingModeTiming.BASE_OR_INDEX
            )
            else -> impossible()
        }

        private fun getDisplacementOnly(cpu: CPU, reg: Int): AddressingMode = with(cpu) {
            val effectiveAddress = memory.getWord(codeOffset + 2)
            return cpu.createDisplacementMemoryMode(effectiveAddress = effectiveAddress, reg = reg)
        }

        private fun getRwIbIndirect(cpu: CPU, rm: Int, reg: Int): Memory {
            val b = cpu.memory.getByte(cpu.codeOffset + 2).toByte()
            return when (rm) {
                0 -> cpu.createRwIbIndirectMemoryMode(
                    segmentRegister = SegmentRegister.DS,
                    effectiveAddress = cpu.registers.get(Register16.BX) + cpu.registers.get(Register16.SI) + b,
                    reg = reg,
                    timing = AddressingModeTiming.BX_SI_DISPLACEMENT
                )
                1 -> cpu.createRwIwIndirectMemoryMode(
                    segmentRegister = SegmentRegister.DS,
                    effectiveAddress = cpu.registers.bx + cpu.registers.di + b,
                    reg = reg,
                    timing = AddressingModeTiming.BX_DI_DISPLACEMENT
                )
                2 -> cpu.createRwIbIndirectMemoryMode(
                    segmentRegister = SegmentRegister.SS,
                    effectiveAddress = cpu.registers.get(Register16.BP) + cpu.registers.get(Register16.SI) + b,
                    reg = reg,
                    timing = AddressingModeTiming.BP_SI_DISPLACEMENT
                )
                3 -> cpu.createRwIbIndirectMemoryMode(
                    segmentRegister = SegmentRegister.SS,
                    effectiveAddress = cpu.registers.get(Register16.BP) + cpu.registers.get(Register16.DI) + b,
                    reg = reg,
                    timing = AddressingModeTiming.BP_DI_DISPLACEMENT
                )
                4 -> cpu.createRwIbIndirectMemoryMode(
                    segmentRegister = SegmentRegister.DS,
                    effectiveAddress = cpu.registers.get(Register16.SI) + b,
                    reg = reg,
                    timing = AddressingModeTiming.BASE_OR_INDEX_DISPLACEMENT
                )
                5 -> cpu.createRwIbIndirectMemoryMode(
                    segmentRegister = SegmentRegister.DS,
                    effectiveAddress = cpu.registers.get(Register16.DI) + b,
                    reg = reg,
                    timing = AddressingModeTiming.BASE_OR_INDEX_DISPLACEMENT
                )
                6 -> cpu.createRwIbIndirectMemoryMode(
                    segmentRegister = SegmentRegister.SS,
                    effectiveAddress = cpu.registers.get(Register16.BP) + b,
                    reg = reg,
                    timing = AddressingModeTiming.BASE_OR_INDEX_DISPLACEMENT
                )
                7 -> cpu.createRwIbIndirectMemoryMode(
                    segmentRegister = SegmentRegister.DS,
                    effectiveAddress = cpu.registers.get(Register16.BX) + b,
                    reg = reg,
                    timing = AddressingModeTiming.BASE_OR_INDEX_DISPLACEMENT
                )
                else -> impossible()
            }
        }

        private fun getRwIwIndirect(cpu: CPU, rm: Int, reg: Int): AddressingMode {
            val w = cpu.memory.getWord(cpu.codeOffset + 2).toShort()
            return when (rm) {
                1 -> cpu.createRwIwIndirectMemoryMode(
                    segmentRegister = SegmentRegister.DS,
                    effectiveAddress = cpu.registers.bx + cpu.registers.di + w,
                    reg = reg,
                    timing = AddressingModeTiming.BX_DI_DISPLACEMENT
                )
                3 -> cpu.createRwIwIndirectMemoryMode(
                    segmentRegister = SegmentRegister.SS,
                    effectiveAddress = cpu.registers.get(Register16.BP) + cpu.registers.get(Register16.DI) + w,
                    reg = reg,
                    timing = AddressingModeTiming.BP_DI_DISPLACEMENT
                )
                4 -> cpu.createRwIwIndirectMemoryMode(
                    segmentRegister = SegmentRegister.DS,
                    effectiveAddress = cpu.registers.get(Register16.SI) + w,
                    reg = reg,
                    timing = AddressingModeTiming.BASE_OR_INDEX_DISPLACEMENT
                )
                5 -> cpu.createRwIwIndirectMemoryMode(
                    segmentRegister = SegmentRegister.DS,
                    effectiveAddress = cpu.registers.get(Register16.DI) + w,
                    reg = reg,
                    timing = AddressingModeTiming.BASE_OR_INDEX_DISPLACEMENT
                )
                6 -> cpu.createRwIwIndirectMemoryMode(
                    segmentRegister = SegmentRegister.SS,
                    effectiveAddress = cpu.registers.get(Register16.BP) + w,
                    reg = reg,
                    timing = AddressingModeTiming.BASE_OR_INDEX_DISPLACEMENT
                )
                7 -> cpu.createRwIwIndirectMemoryMode(
                    segmentRegister = SegmentRegister.DS,
                    effectiveAddress = cpu.registers.get(Register16.BX) + w,
                    reg = reg,
                    timing = AddressingModeTiming.BASE_OR_INDEX_DISPLACEMENT
                )
                else -> TODO("$rm")
            }
        }

        private fun CPU.createRegisterIndirectMemoryMode(
            segmentRegister: SegmentRegister,
            effectiveAddress: Int,
            reg: Int,
            timing: AddressingModeTiming
        ) = Memory(
            address = makeAddress16(segmentRegister, effectiveAddress),
            effectiveAddress = effectiveAddress,
            reg = reg,
            byteCount = 2,
            clocks = timing.clocks
        )

        private fun CPU.createRwIbIndirectMemoryMode(
            segmentRegister: SegmentRegister,
            effectiveAddress: Int,
            reg: Int,
            timing: AddressingModeTiming,
        ) = Memory(
            address = makeAddress16(segmentRegister, effectiveAddress),
            effectiveAddress = effectiveAddress,
            reg = reg,
            byteCount = 3,
            clocks = timing.clocks
        )

        private fun CPU.createRwIwIndirectMemoryMode(
            segmentRegister: SegmentRegister,
            effectiveAddress: Int,
            reg: Int,
            timing: AddressingModeTiming,
        ) = Memory(
            address = makeAddress16(segmentRegister, effectiveAddress),
            effectiveAddress = effectiveAddress,
            reg = reg,
            byteCount = 4,
            clocks = timing.clocks
        )

        private fun CPU.createDisplacementMemoryMode(
            effectiveAddress: Int,
            reg: Int,
        ) = Memory(
            address = makeAddress16(SegmentRegister.DS, effectiveAddress),
            effectiveAddress = effectiveAddress,
            clocks = AddressingModeTiming.DISPLACEMENT.clocks,
            reg = reg,
            byteCount = 4,
        )

    }

}