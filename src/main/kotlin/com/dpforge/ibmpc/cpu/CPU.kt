package com.dpforge.ibmpc.cpu

import com.dpforge.ibmpc.ALU
import com.dpforge.ibmpc.DMA
import com.dpforge.ibmpc.memory.Memory
import com.dpforge.ibmpc.PIC
import com.dpforge.ibmpc.PIT
import com.dpforge.ibmpc.port.Ports
import com.dpforge.ibmpc.cpu.instruction.*
import com.dpforge.ibmpc.cpu.timing.Timing
import com.dpforge.ibmpc.cpu.timing.Timing.StringOperationTiming
import com.dpforge.ibmpc.extensions.higherWord
import com.dpforge.ibmpc.extensions.lowerWord
import com.dpforge.ibmpc.extensions.toHex
import com.dpforge.ibmpc.utils.impossible
import org.slf4j.LoggerFactory

class CPU(
    val memory: Memory,
    val ports: Ports,
    val pic: PIC,
    val pit: PIT,
    val dma: DMA,
) {

    private val logger = LoggerFactory.getLogger("CPU")

    private var overriddenSegmentRegister: SegmentRegister? = null
    private var repMode: RepMode? = null

    val registers = Registers()
    val alu = ALU(registers.flags)

    var haltState: Boolean = false

    private var clocks: Int = 0

    fun reset() {
        registers.cs = 0xf000
        registers.ip = 0xfff0
    }

    fun executeNext() {
        if (haltState) {
            TODO("Need to handle this somehow")
        }

        if (registers.flags.getFlag(FlagsRegister.INTERRUPT_ENABLED_FLAG)) {
            pic.getPendingInterrupt()?.let { interrupt ->
                callInterruptHandler(interrupt)
            }
        }

        val executingIp = registers.ip
        try {
            handleOpcodeAtCodeOffset()
        } catch (ie: InterruptException) {
            registers.ip = executingIp
            callInterruptHandler(ie.type.interrupt)
        }

        while (clocks > 3) {
            clocks -= 4
            pit.update()
            dma.onCPUCycle()
        }
    }

    private fun handleOpcodeAtCodeOffset() {
        val builder = StringBuilder("[${codeOffset.toHex()}]")
        try {
            do {
                val opcode = memory.getByte(codeOffset)
                builder.append(" ${DebugUtils.getMnemonic(opcode)} (${opcode.toHex()})")
                val handled = handleOpcode(opcode)
            } while (!handled)
        } finally {
            //logger.debug(builder.toString())
        }
    }

    private fun handleOpcode(opcode: Int): Boolean {
        var handled = true
        clocks += when (opcode) {
            0x00 -> ADD.rmbRb(this)
            0x01 -> ADD.rmwRw(this)
            0x02 -> ADD.rbRmb(this)
            0x03 -> ADD.rwRmw(this)
            0x04 -> ADD.alIb(this)
            0x05 -> ADD.axIw(this)
            0x06 -> PUSH.segmentRegister(opcode, this)
            0x07 -> POP.segmentRegister(opcode, this)
            0x08 -> OR.rmbRb(this)
            0x09 -> OR.rmwRw(this)
            0x0A -> OR.rbRmb(this)
            0x0B -> OR.rwRmw(this)
            0x0C -> OR.alIb(this)
            0x0D -> OR.axIw(this)
            0x0E -> PUSH.segmentRegister(opcode, this)
            0x0F -> error("Opcode ${opcode.toHex()} is no supported on 8086")
            0x10 -> ADC.rmbRb(this)
            0x11 -> ADC.rmwRw(this)
            0x12 -> ADC.rbRmb(this)
            0x13 -> ADC.rwRmw(this)
            0x14 -> ADC.alIb(this)
            0x15 -> ADC.axIw(this)
            0x16 -> PUSH.segmentRegister(opcode, this)
            0x17 -> POP.segmentRegister(opcode, this)
            0x18 -> SBB.rmbRb(this)
            0x19 -> SBB.rmwRw(this)
            0x1A -> SBB.rbRmb(this)
            0x1B -> SBB.rwRmw(this)
            0x1C -> SBB.alIb(this)
            0x1D -> SBB.axIw(this)
            0x1E -> PUSH.segmentRegister(opcode, this)
            0x1F -> POP.segmentRegister(opcode, this)
            0x20 -> AND.rmbRb(this)
            0x21 -> AND.rmwRw(this)
            0x22 -> AND.rbRmb(this)
            0x23 -> AND.rwRmw(this)
            0x24 -> AND.alIb(this)
            0x25 -> AND.axIw(this)
            0x26 -> {
                handled = false
                overrideSegmentRegister(SegmentRegister.ES)
                Timing.segmentOverride()
            }
            0x27 -> DAA.daa(this)
            0x28 -> SUB.rmbRb(this)
            0x29 -> SUB.rmwRw(this)
            0x2A -> SUB.rbRmb(this)
            0x2B -> SUB.rwRmw(this)
            0x2C -> SUB.alIb(this)
            0x2D -> SUB.axIw(this)
            0x2E -> {
                handled = false
                overrideSegmentRegister(SegmentRegister.CS)
                Timing.segmentOverride()
            }
            0x2F -> DAS.das(this)
            0x30 -> XOR.rmbRb(this)
            0x31 -> XOR.rmwRw(this)
            0x32 -> XOR.rbRmb(this)
            0x33 -> XOR.rwRmw(this)
            0x34 -> XOR.alIb(this)
            0x35 -> XOR.axIw(this)
            0x36 -> {
                handled = false
                overrideSegmentRegister(SegmentRegister.SS)
                Timing.segmentOverride()
            }
            0x37 -> AAA.aaa(this)
            0x38 -> CMP.rmbRb(this)
            0x39 -> CMP.rmwRw(this)
            0x3A -> CMP.rbRmb(this)
            0x3B -> CMP.rwRmw(this)
            0x3C -> CMP.alIb(this)
            0x3D -> CMP.axIw(this)
            0x3E -> {
                handled = false
                overrideSegmentRegister(SegmentRegister.DS)
                Timing.segmentOverride()
            }
            0x3F -> AAS.aas(this)
            in 0x40..0x47 -> INC.reg16(opcode, this)
            in 0x48..0x4F -> DEC.reg16(opcode, this)
            in 0x50..0x57 -> PUSH.reg16(opcode, this)
            in 0x58..0x5f -> POP.reg16(opcode, this)
            in 0x60..0x65 -> error("Opcode ${opcode.toHex()} is no supported on 8086")
            0x66 -> TODO("Operand-size override prefix")
            0x67 -> TODO("Address-size override prefix")
            in 0x68..0x6F -> error("Opcode ${opcode.toHex()} is no supported on 8086")
            0x70 -> JO.short(this)
            0x71 -> JNO.short(this)
            0x72 -> JB.short(this)
            0x73 -> JNC.short(this)
            0x74 -> JE.short(this)
            0x75 -> JNE.short(this)
            0x76 -> JBE.short(this)
            0x77 -> JA.short(this)
            0x78 -> JS.short(this)
            0x79 -> JNS.short(this)
            0x7A -> JP.short(this)
            0x7B -> JNP.short(this)
            0x7C -> JL.short(this)
            0x7D -> JGE.short(this)
            0x7E -> JLE.short(this)
            0x7F -> JG.short(this)
            0x80 -> {
                val b = memory.getByte(codeOffset + 1)
                when (b.additionalOpcode) {
                    0x00 -> ADD.rmbIb(this)
                    0x01 -> OR.rmbIb(this)
                    0x02 -> ADC.rmbIb(this)
                    0x03 -> SBB.rmbIb(this)
                    0x04 -> AND.rmbIb(this)
                    0x05 -> SUB.rmbIb(this)
                    0x06 -> XOR.rmbIb(this)
                    0x07 -> CMP.rmbIb(this)
                    else -> error("Unsupported opcode2 ${b.additionalOpcode.toHex()}")
                }
            }
            0x81 -> {
                val b = memory.getByte(codeOffset + 1)
                when (b.additionalOpcode) {
                    0x00 -> ADD.rmwIw(this)
                    0x01 -> OR.rmwIw(this)
                    0x02 -> ADC.rmwIw(this)
                    0x03 -> SBB.rmwIw(this)
                    0x04 -> AND.rmwIw(this)
                    0x05 -> SUB.rmwIw(this)
                    0x06 -> XOR.rmwIw(this)
                    0x07 -> CMP.rmwIw(this)
                    else -> impossible()
                }
            }
            0x82 -> TODO("Arithmetic and logical instructions")
            0x83 -> {
                val b = memory.getByte(codeOffset + 1)
                when (b.additionalOpcode) {
                    0x00 -> ADD.rmwIb(this)
                    0x01 -> error("Opcode ${opcode.toHex()}/${b.additionalOpcode} is no supported on 8086")
                    0x02 -> ADC.rmwIb(this)
                    0x03 -> SBB.rmwIb(this)
                    0x04 -> AND.rmwIb(this) // undocumented for 8086
                    0x05 -> SUB.rmwIb(this)
                    0x06 -> error("Opcode ${opcode.toHex()}/${b.additionalOpcode} is no supported on 8086")
                    0x07 -> CMP.rmwIb(this)
                    else -> impossible()
                }
            }
            0x84 -> TEST.rmbRb(this)
            0x85 -> TEST.rmwRw(this)
            0x86 -> XCHG.rbRmb(this)
            0x87 -> XCHG.rwRmw(this)
            0x88 -> MOV.rmbRb(this)
            0x89 -> MOV.rmwRw(this)
            0x8A -> MOV.rbRmb(this)
            0x8B -> MOV.rwRmw(this)
            0x8C -> MOV.rmwSr(this)
            0x8D -> LEA.lea(this)
            0x8E -> MOV.srRmw(this)
            0x8F -> POP.rmw(this)
            in 0x90..0x97 -> XCHG.axRw(opcode, this) // 0x90 can be also treated as NOP
            0x98 -> CBW.cbw(this)
            0x99 -> CWD.cwd(this)
            0x9A -> CALL.far(this)
            0x9B -> TODO("FWAIT")
            0x9C -> PUSH.flags(this)
            0x9D -> POP.flags(this)
            0x9E -> FlagInstructions.sahf(this)
            0x9F -> FlagInstructions.lahf(this)
            0xA0 -> MOV.alMb(this)
            0xA1 -> MOV.axMw(this)
            0xA2 -> MOV.mbAl(this)
            0xA3 -> MOV.mwAx(this)
            0xA4 -> MOVS.b(this)
            0xA5 -> MOVS.w(this)
            0xA6 -> CompareStrings.cmpsb(this)
            0xA7 -> CompareStrings.cmpsw(this)
            0xA8 -> TEST.alIb(this)
            0xA9 -> TEST.axIw(this)
            0xAA -> STOS.b(this)
            0xAB -> STOS.w(this)
            0xAC -> LODS.b(this)
            0xAD -> LODS.w(this)
            0xAE -> SCAS.b(this)
            0xAF -> SCAS.w(this)
            in 0xB0..0xB7 -> MOV.rbIb(opcode, this)
            in 0xB8..0xBF -> MOV.rwIw(opcode, this)
            0xC0 -> error("Opcode ${opcode.toHex()} is no supported on 8086")
            0xC1 -> error("Opcode ${opcode.toHex()} is no supported on 8086")
            0xC2 -> Return.retnIw(this)
            0xC3 -> Return.ret(this)
            0xC4 -> LoadFarPointer.les(this)
            0xC5 -> LoadFarPointer.lds(this)
            0xC6 -> MOV.rmbIb(this)
            0xC7 -> MOV.rmwIw(this)
            0xC8 -> error("Opcode ${opcode.toHex()} is no supported on 8086")
            0xC9 -> error("Opcode ${opcode.toHex()} is no supported on 8086")
            0xCA -> Return.retfIw(this)
            0xCB -> Return.retf(this)
            0xCC -> TODO("INT 3")
            0xCD -> INT.ib(this)
            0xCE -> INT.into(this)
            0xCF -> Return.iret(this)
            0xD0 -> {
                val b = memory.getByte(codeOffset + 1)
                when (b.additionalOpcode) {
                    0x00 -> ROL.rmb1(this)
                    0x01 -> ROR.rmb1(this)
                    0x02 -> RCL.rmb1(this)
                    0x03 -> RCR.rmb1(this)
                    0x04 -> SHL.rmb1(this)
                    0x05 -> SHR.rmb1(this)
                    0x06 -> error("Undocumented")
                    0x07 -> SAR.rmb1(this)
                    else -> impossible()
                }
            }
            0xD1 -> {
                val b = memory.getByte(codeOffset + 1)
                when (b.additionalOpcode) {
                    0x00 -> ROL.rmw1(this)
                    0x01 -> ROR.rmw1(this)
                    0x02 -> RCL.rmw1(this)
                    0x03 -> RCR.rmw1(this)
                    0x04 -> SHL.rmw1(this)
                    0x05 -> SHR.rmw1(this)
                    0x06 -> error("Undocumented")
                    0x07 -> SAR.rmw1(this)
                    else -> impossible()
                }
            }
            0xD2 -> {
                val b = memory.getByte(codeOffset + 1)
                val count = registers.cl and 0b11111 // 8086 does not mask count but tests expect it
                when (b.additionalOpcode) {
                    0x00 -> ROL.rmb(this, count)
                    0x01 -> ROR.rmb(this, count)
                    0x02 -> RCL.rmb(this, count)
                    0x03 -> RCR.rmb(this, count)
                    0x04 -> SHL.rmb(this, count)
                    0x05 -> SHR.rmb(this, count)
                    0x06 -> error("Undocumented")
                    0x07 -> SAR.rmb(this, count)
                    else -> impossible()
                }
            }
            0xD3 -> {
                val b = memory.getByte(codeOffset + 1)
                val count = registers.cl and 0b11111 // 8086 does not mask count but tests expect it
                when (b.additionalOpcode) {
                    0x00 -> ROL.rmw(this, count)
                    0x01 -> ROR.rmw(this, count)
                    0x02 -> RCL.rmw(this, count)
                    0x03 -> RCR.rmw(this, count)
                    0x04 -> SHL.rmw(this, count)
                    0x05 -> SHR.rmw(this, count)
                    0x06 -> error("Undocumented")
                    0x07 -> SAR.rmw(this, count)
                    else -> impossible()
                }
            }
            0xD4 -> AAM.aam(this)
            0xD5 -> AAD.aad(this)
            0xD6 -> {
                // Undefined and Reserved; Does not Generate #UD
                registers.ip += 1
                Timing.undefined()
            }
            0xD7 -> XLAT.xlat(this)
            in 0xD8..0xDF -> {
                val addressingMode = AddressingMode.getForCurrentCodeOffset(this)
                logger.warn("Floating-point arithmetic opcode ${opcode.toHex()} skipped")
                registers.ip += addressingMode.byteCount
                when (addressingMode) {
                    is AddressingMode.Register -> 0
                    is AddressingMode.Memory -> addressingMode.clocks
                }
            }
            0xE0 -> LOOPNZ.short(this)
            0xE1 -> LOOPZ.short(this)
            0xE2 -> LOOP.short(this)
            0xE3 -> JCXZ.short(this)
            0xE4 -> IN.alIb(this)
            0xE5 -> IN.axIb(this)
            0xE6 -> OUT.alIb(this)
            0xE7 -> TODO("OUT imm8 AX")
            0xE8 -> CALL.near(this)
            0xE9 -> JMP.near(this)
            0xEA -> JMPF.iww(this)
            0xEB -> JMP.short(this)
            0xEC -> IN.alDX(this)
            0xED -> IN.axDX(this)
            0xEE -> OUT.dxAl(this)
            0xEF -> OUT.dxAx(this)
            0xF0 -> TODO("LOCK")
            0xF1 -> {
                // Undefined and Reserved; Does not Generate #UD
                registers.ip += 1
                Timing.undefined()
            }
            0xF2 -> {
                handled = false
                repMode = RepMode.REPNZ
                registers.ip += 1
                Timing.repeat()
            }
            0xF3 -> {
                handled = false
                repMode = RepMode.REP_REPZ
                registers.ip += 1
                Timing.repeat()
            }
            0xF4 -> HLT.hlt(this)
            0xF5 -> FlagInstructions.cmc(this)
            0xF6 -> {
                val b = memory.getByte(codeOffset + 1)
                when (b.additionalOpcode) {
                    0x00 -> TEST.rmbIb(this)
                    0x01 -> error("Undocumented TEST")
                    0x02 -> NOT.rmb(this)
                    0x03 -> NEG.rmb(this)
                    0x04 -> MUL.rmb(this)
                    0x05 -> IMUL.rmb(this)
                    0x06 -> DIV.rmb(this)
                    0x07 -> IDIV.rmb(this)
                    else -> impossible()
                }
            }
            0xF7 -> {
                val b = memory.getByte(codeOffset + 1)
                when (b.additionalOpcode) {
                    0x00 -> TEST.rmwIw(this)
                    0x01 -> error("Undocumented TEST")
                    0x02 -> NOT.rmw(this)
                    0x03 -> NEG.rmw(this)
                    0x04 -> MUL.rmw(this)
                    0x05 -> IMUL.rmw(this)
                    0x06 -> DIV.rmw(this)
                    0x07 -> IDIV.rmw(this)
                    else -> impossible()
                }
            }
            0xF8 -> FlagInstructions.clc(this)
            0xF9 -> FlagInstructions.stc(this)
            0xFA -> FlagInstructions.cli(this)
            0xFB -> FlagInstructions.sti(this)
            0xFC -> FlagInstructions.cld(this)
            0xFD -> FlagInstructions.std(this)
            0xFE -> {
                val b = memory.getByte(codeOffset + 1)
                when (b.additionalOpcode) {
                    0x00 -> INC.rmb(this)
                    0x01 -> DEC.rmb(this)
                    else -> impossible()
                }
            }
            0xFF -> {
                val b = memory.getByte(codeOffset + 1)
                when (b.additionalOpcode) {
                    0x00 -> INC.rmw(this)
                    0x01 -> DEC.rmw(this)
                    0x02 -> CALL.rmw(this)
                    0x03 -> CALLF.mww(this)
                    0x04 -> JMP.rmw(this)
                    0x05 -> JMPF.mww(this)
                    0x06 -> PUSH.rmw(this)
                    else -> impossible()
                }
            }
            else -> error(
                "Unsupported opcode ${opcode.toHex()}. " +
                        "Check here http://ref.x86asm.net/coder32.html#x${opcode.toHex(prefix = "").toUpperCase()}"
            )
        }
        if (handled) {
            overriddenSegmentRegister = null
            repMode = null
        }
        return handled
    }

    fun callSoftwareInterrupt(interrupt: Int) {
        callInterruptHandler(interrupt)
    }

    private fun callInterruptHandler(interrupt: Int) {
        val handlerAddress = memory.getDoubleWord(interrupt * 4)
        push16(registers.flags.value16)
        push16(registers.cs)
        push16(registers.ip)
        registers.flags.setFlag(FlagsRegister.INTERRUPT_ENABLED_FLAG, false)
        registers.cs = handlerAddress.higherWord
        registers.ip = handlerAddress.lowerWord
    }

    fun push16(value: Int) {
        registers.sp -= 2
        memory.setWord(stackOffset, value)
    }

    fun pop16(): Int {
        val value = memory.getWord(stackOffset)
        registers.sp += 2
        return value
    }

    fun stringOperation(timing: StringOperationTiming, checkZeroFlag: Boolean, block: CPU.() -> Unit): Int {
        val repMode = this.repMode
        if (repMode == null) {
            this.block()
            registers.ip += 1
            return timing.singleExecution
        }

        var totalClocks = 0
        while (registers.cx != 0) {
            this.block()
            totalClocks += timing.repeatExecution
            registers.cx -= 1
            val zf = registers.flags.getFlag(FlagsRegister.ZERO_FLAG)
            if (checkZeroFlag) {
                when (repMode) {
                    RepMode.REP_REPZ -> {
                        if (!zf) {
                            break
                        }
                    }
                    RepMode.REPNZ -> {
                        if (zf) {
                            break
                        }
                    }
                }
            }
        }
        registers.ip += 1
        return totalClocks
    }

    val codeOffset: Int
        get() = registers.cs * 16 + registers.ip

    val stackOffset: Int
        get() = registers.ss * 16 + registers.sp

    private fun overrideSegmentRegister(reg: SegmentRegister) {
        overriddenSegmentRegister = reg
        registers.ip += 1
    }

    private inline val Int.additionalOpcode: Int
        get() = (this shr 3) and 0b111

    fun makeAddress16(register: SegmentRegister, offset: Int) =
        registers.get(getOverriddenSegmentRegisterOr(register)) * 16 + (offset and 0xFFFF)

    fun makeAddress16(segmentRegister: SegmentRegister, register16: Register16) =
        makeAddress16(segmentRegister, registers.get(register16))

    fun makeAddress16IgnoreOverride(segmentRegister: SegmentRegister, register16: Register16) =
        registers.get(segmentRegister) * 16 + registers.get(register16)

    private fun getOverriddenSegmentRegisterOr(reg: SegmentRegister): SegmentRegister =
        overriddenSegmentRegister ?: reg

    private enum class RepMode {
        REP_REPZ,
        REPNZ
    }
}