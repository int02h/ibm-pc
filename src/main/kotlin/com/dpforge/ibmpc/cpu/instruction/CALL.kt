package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.AddressingMode
import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.Register16
import com.dpforge.ibmpc.cpu.timing.CallType
import com.dpforge.ibmpc.cpu.timing.Timing

object CALL {

    fun near(cpu: CPU): Int = with(cpu) {
        val offset = memory.getWord(codeOffset + 1).toShort()
        registers.ip += 3 // for the CALL itself
        push16(registers.ip)
        registers.ip = registers.ip + offset
        Timing.call(CallType.Near)
    }

    fun far(cpu: CPU): Int = with(cpu) {
        val offset = codeOffset
        registers.ip += 5 // for the CALL itself
        push16(registers.cs)
        push16(registers.ip)
        registers.ip = memory.getWord(offset + 1)
        registers.cs = memory.getWord(offset + 3)
        Timing.call(CallType.Far)
    }

    fun rmw(cpu: CPU): Int = with(cpu) {
        val instructionType: CallType
        val addressingMode = AddressingMode.getForCurrentCodeOffset(this)

        registers.ip += addressingMode.byteCount
        push16(registers.ip)

        registers.ip = when (addressingMode) {
            is AddressingMode.Register -> {
                instructionType = CallType.Reg
                registers.get(Register16.get(addressingMode.index))
            }
            is AddressingMode.Memory -> {
                instructionType = CallType.Mem16(addressingMode)
                memory.getWord(addressingMode.address)
            }
        }

        Timing.call(instructionType)
    }

}