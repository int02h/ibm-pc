package com.dpforge.ibmpc.cpu.timing

import com.dpforge.ibmpc.cpu.AddressingMode

object Timing {

    fun aaa() = 8
    fun aad() = 60
    fun aam() = 83
    fun aas() = 8
    fun cbw() = 2
    fun clc() = 2
    fun cld() = 2
    fun cli() = 2
    fun cmc() = 2
    fun stc() = 2
    fun std() = 2
    fun sti() = 2
    fun sahf() = 4
    fun lahf() = 4
    fun cwd() = 5
    fun daa() = 4
    fun das() = 4
    fun hlt() = 2
    fun xlat() = 11

    fun inAccImm() = 14
    fun inAccDx() = 12
    fun outImmAcc() = 14
    fun outDxAcc() = 12

    fun intoSkipped() = 4
    fun intoCalled() = 73
    fun intImm() = 71

    fun pushSeg() = 14
    fun pushf() = 14
    fun popSeg() = 12
    fun popf() = 12

    fun retn() = 20
    fun retnImm() = 24
    fun retf() = 34
    fun retfImm() = 33
    fun iret() = 44

    fun segmentOverride() = 2
    fun undefined() = 2
    fun repeat() = 9

    fun adc(instructionType: BinaryInstructionType) = when (instructionType) {
        is BinaryInstructionType.RegReg -> 3
        is BinaryInstructionType.MemReg -> 24 + instructionType.addressingMode.clocks
        is BinaryInstructionType.RegMem -> 13 + instructionType.addressingMode.clocks
        is BinaryInstructionType.RegImm -> 4
        is BinaryInstructionType.MemImm -> 23 + instructionType.addressingMode.clocks
        is BinaryInstructionType.AccImm -> 4
    }

    fun add(instructionType: BinaryInstructionType) = when (instructionType) {
        is BinaryInstructionType.RegReg -> 3
        is BinaryInstructionType.MemReg -> 24 + instructionType.addressingMode.clocks
        is BinaryInstructionType.RegMem -> 13 + instructionType.addressingMode.clocks
        is BinaryInstructionType.RegImm -> 4
        is BinaryInstructionType.MemImm -> 23 + instructionType.addressingMode.clocks
        is BinaryInstructionType.AccImm -> 4
    }

    fun sbb(instructionType: BinaryInstructionType) = when (instructionType) {
        is BinaryInstructionType.RegReg -> 3
        is BinaryInstructionType.MemReg -> 24 + instructionType.addressingMode.clocks
        is BinaryInstructionType.RegMem -> 13 + instructionType.addressingMode.clocks
        is BinaryInstructionType.RegImm -> 4
        is BinaryInstructionType.MemImm -> 23 + instructionType.addressingMode.clocks
        is BinaryInstructionType.AccImm -> 4
    }

    fun sub(instructionType: BinaryInstructionType) = when (instructionType) {
        is BinaryInstructionType.RegReg -> 3
        is BinaryInstructionType.MemReg -> 24 + instructionType.addressingMode.clocks
        is BinaryInstructionType.RegMem -> 13 + instructionType.addressingMode.clocks
        is BinaryInstructionType.RegImm -> 4
        is BinaryInstructionType.MemImm -> 23 + instructionType.addressingMode.clocks
        is BinaryInstructionType.AccImm -> 4
    }

    fun and(instructionType: BinaryInstructionType) = when (instructionType) {
        is BinaryInstructionType.RegReg -> 3
        is BinaryInstructionType.MemReg -> 24 + instructionType.addressingMode.clocks
        is BinaryInstructionType.RegMem -> 13 + instructionType.addressingMode.clocks
        is BinaryInstructionType.RegImm -> 4
        is BinaryInstructionType.MemImm -> 23 + instructionType.addressingMode.clocks
        is BinaryInstructionType.AccImm -> 4
    }

    fun call(instructionType: CallType) = when (instructionType) {
        is CallType.Near -> 23
        is CallType.Reg -> 20
        is CallType.Mem16 -> 29 + instructionType.addressingMode.clocks
        is CallType.Far -> 36
        is CallType.Mem32 -> 53 + instructionType.addressingMode.clocks
    }

    fun cmp(instructionType: BinaryInstructionType) = when (instructionType) {
        is BinaryInstructionType.RegReg -> 3
        is BinaryInstructionType.MemReg -> 13 + instructionType.addressingMode.clocks
        is BinaryInstructionType.RegMem -> 13 + instructionType.addressingMode.clocks
        is BinaryInstructionType.RegImm -> 4
        is BinaryInstructionType.MemImm -> 14 + instructionType.addressingMode.clocks
        is BinaryInstructionType.AccImm -> 4
    }

    fun dec(instructionType: UnaryInstructionType) = when (instructionType) {
        is UnaryInstructionType.Reg8 -> 3
        is UnaryInstructionType.Reg16 -> 3
        is UnaryInstructionType.Mem8 -> 23 + instructionType.addressingMode.clocks
        is UnaryInstructionType.Mem16 -> 23 + instructionType.addressingMode.clocks
    }

    fun inc(instructionType: UnaryInstructionType) = when (instructionType) {
        is UnaryInstructionType.Reg8 -> 3
        is UnaryInstructionType.Reg16 -> 3
        is UnaryInstructionType.Mem8 -> 23 + instructionType.addressingMode.clocks
        is UnaryInstructionType.Mem16 -> 23 + instructionType.addressingMode.clocks
    }

    fun jmp(instructionType: JmpType) = when (instructionType) {
        is JmpType.Short -> 15
        is JmpType.Near -> 15
        is JmpType.Far -> 15
        is JmpType.Reg16 -> 11
        is JmpType.Mem16 -> 18 + instructionType.addressingMode.clocks
        is JmpType.Mem32 -> 24 + instructionType.addressingMode.clocks
    }

    fun mov(instructionType: MovType) = when (instructionType) {
        is MovType.RegReg -> 2
        is MovType.MemReg -> 13 + instructionType.addressingMode.clocks
        is MovType.RegMem -> 12 + instructionType.addressingMode.clocks
        is MovType.MemImm -> 14 + instructionType.addressingMode.clocks
        is MovType.RegImm -> 4
        is MovType.AccMem -> 14
        is MovType.MemAcc -> 14
        is MovType.SRegReg -> 2
        is MovType.SRegMem -> 12 + instructionType.addressingMode.clocks
        is MovType.RegSReg -> 2
        is MovType.MemSReg -> 13 + instructionType.addressingMode.clocks
    }

    fun neg(instructionType: UnaryInstructionType) = when (instructionType) {
        is UnaryInstructionType.Reg8 -> 3
        is UnaryInstructionType.Reg16 -> 3
        is UnaryInstructionType.Mem8 -> 24 + instructionType.addressingMode.clocks
        is UnaryInstructionType.Mem16 -> 24 + instructionType.addressingMode.clocks
    }

    fun not(instructionType: UnaryInstructionType) = when (instructionType) {
        is UnaryInstructionType.Reg8 -> 3
        is UnaryInstructionType.Reg16 -> 3
        is UnaryInstructionType.Mem8 -> 24 + instructionType.addressingMode.clocks
        is UnaryInstructionType.Mem16 -> 24 + instructionType.addressingMode.clocks
    }

    fun or(instructionType: BinaryInstructionType) = when (instructionType) {
        is BinaryInstructionType.RegReg -> 3
        is BinaryInstructionType.MemReg -> 24 + instructionType.addressingMode.clocks
        is BinaryInstructionType.RegMem -> 13 + instructionType.addressingMode.clocks
        is BinaryInstructionType.RegImm -> 4
        is BinaryInstructionType.MemImm -> 23 + instructionType.addressingMode.clocks
        is BinaryInstructionType.AccImm -> 4
    }

    fun xor(instructionType: BinaryInstructionType) = when (instructionType) {
        is BinaryInstructionType.RegReg -> 3
        is BinaryInstructionType.MemReg -> 24 + instructionType.addressingMode.clocks
        is BinaryInstructionType.RegMem -> 13 + instructionType.addressingMode.clocks
        is BinaryInstructionType.RegImm -> 4
        is BinaryInstructionType.MemImm -> 23 + instructionType.addressingMode.clocks
        is BinaryInstructionType.AccImm -> 4
    }

    fun push(instructionType: UnaryInstructionType) = when (instructionType) {
        is UnaryInstructionType.Reg8 -> 15
        is UnaryInstructionType.Reg16 -> 15
        is UnaryInstructionType.Mem8 -> 24 + instructionType.addressingMode.clocks
        is UnaryInstructionType.Mem16 -> 24 + instructionType.addressingMode.clocks
    }

    fun pop(instructionType: UnaryInstructionType) = when (instructionType) {
        is UnaryInstructionType.Reg8 -> 12
        is UnaryInstructionType.Reg16 -> 12
        is UnaryInstructionType.Mem8 -> 25 + instructionType.addressingMode.clocks
        is UnaryInstructionType.Mem16 -> 25 + instructionType.addressingMode.clocks
    }

    fun rotateBits(instructionType: BitShiftType) = when (instructionType) {
        is BitShiftType.Reg1 -> 2
        is BitShiftType.Mem1 -> 23 + instructionType.addressingMode.clocks
        is BitShiftType.RegCL -> 8 + 4 * instructionType.count
        is BitShiftType.MemCL -> 28 + 4 * instructionType.count + instructionType.addressingMode.clocks
    }

    fun shiftBits(instructionType: BitShiftType) = when (instructionType) {
        is BitShiftType.Reg1 -> 2
        is BitShiftType.Mem1 -> 23 + instructionType.addressingMode.clocks
        is BitShiftType.RegCL -> 8 + 4 * instructionType.count
        is BitShiftType.MemCL -> 28 + 4 * instructionType.count + instructionType.addressingMode.clocks
    }

    fun test(instructionType: BinaryInstructionType) = when (instructionType) {
        is BinaryInstructionType.RegReg -> 3
        is BinaryInstructionType.MemReg -> 13 + instructionType.addressingMode.clocks
        is BinaryInstructionType.RegMem -> 13 + instructionType.addressingMode.clocks
        is BinaryInstructionType.RegImm -> 5
        is BinaryInstructionType.MemImm -> 11 + instructionType.addressingMode.clocks
        is BinaryInstructionType.AccImm -> 4
    }

    fun xchg(instructionType: XCHGType) = when (instructionType) {
        is XCHGType.RegReg -> 4
        is XCHGType.MemReg -> 25 + instructionType.addressingMode.clocks
        is XCHGType.RegMem -> 25 + instructionType.addressingMode.clocks
        is XCHGType.AccReg -> 3
    }

    fun mul(instructionType: UnaryInstructionType) = when (instructionType) {
        is UnaryInstructionType.Reg8 -> (77 - 70) / 2
        is UnaryInstructionType.Reg16 -> (133 - 118) / 2
        is UnaryInstructionType.Mem8 -> (83 - 76) / 2 + instructionType.addressingMode.clocks
        is UnaryInstructionType.Mem16 -> (139 - 124) / 2 + instructionType.addressingMode.clocks
    }

    fun imul(instructionType: UnaryInstructionType) = when (instructionType) {
        is UnaryInstructionType.Reg8 -> (98 - 80) / 2
        is UnaryInstructionType.Reg16 -> (154 - 128) / 2
        is UnaryInstructionType.Mem8 -> (104 - 86) / 2 + instructionType.addressingMode.clocks
        is UnaryInstructionType.Mem16 -> (160 - 134) / 2 + instructionType.addressingMode.clocks
    }

    fun div(instructionType: UnaryInstructionType) = when (instructionType) {
        is UnaryInstructionType.Reg8 -> (90 - 80) / 2
        is UnaryInstructionType.Reg16 -> (162 - 144) / 2
        is UnaryInstructionType.Mem8 -> (96 - 86) / 2 + instructionType.addressingMode.clocks
        is UnaryInstructionType.Mem16 -> (168 - 150) / 2 + instructionType.addressingMode.clocks
    }

    fun idiv(instructionType: UnaryInstructionType) = when (instructionType) {
        is UnaryInstructionType.Reg8 -> (112 - 101) / 2
        is UnaryInstructionType.Reg16 -> (184 - 165) / 2
        is UnaryInstructionType.Mem8 -> (118 - 107) / 2 + instructionType.addressingMode.clocks
        is UnaryInstructionType.Mem16 -> (190 - 171) / 2 + instructionType.addressingMode.clocks
    }

    fun lea(addressingMode: AddressingMode.Memory) = 2 + addressingMode.clocks
    fun loadFarPointer(addressingMode: AddressingMode.Memory) = 24 + addressingMode.clocks

    sealed class ConditionalTiming(val noJumpCycles: Int, val jumpCycle: Int) {
        object JCC : ConditionalTiming(noJumpCycles = 4, jumpCycle = 16)
        object LOOP : ConditionalTiming(noJumpCycles = 5, jumpCycle = 17)
        object LOOPZ : ConditionalTiming(noJumpCycles = 6, jumpCycle = 18)
        object LOOPNZ : ConditionalTiming(noJumpCycles = 5, jumpCycle = 19)
    }

    sealed class StringOperationTiming(val singleExecution: Int, val repeatExecution: Int) {
        object CMPSB : StringOperationTiming(30, 30)
        object CMPSW : StringOperationTiming(30, 30)
        object LODSB : StringOperationTiming(16, 16)
        object LODSW : StringOperationTiming(16, 16)
        object MOVSB : StringOperationTiming(18, 17)
        object MOVSW : StringOperationTiming(26, 25)
        object SCASB : StringOperationTiming(19, 15)
        object SCASW : StringOperationTiming(19, 19)
        object STOSB : StringOperationTiming(11, 10)
        object STOSW : StringOperationTiming(15, 14)
    }

    enum class AddressingModeTiming(val clocks: Int) {
        BX_SI(7),
        BX_DI(8),
        BP_SI(8),
        BP_DI(7),
        BX_SI_DISPLACEMENT(11),
        BX_DI_DISPLACEMENT(12),
        BP_SI_DISPLACEMENT(12),
        BP_DI_DISPLACEMENT(11),
        BASE_OR_INDEX(5),
        BASE_OR_INDEX_DISPLACEMENT(9),
        DISPLACEMENT(6)
    }

}