package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.AddressingMode
import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.Register16
import com.dpforge.ibmpc.cpu.SegmentRegister
import com.dpforge.ibmpc.extensions.higherWord
import com.dpforge.ibmpc.extensions.lowerWord

object LoadFarPointer {

    fun lds(cpu: CPU) {
        load(SegmentRegister.DS, cpu)
    }

    fun les(cpu: CPU) {
        load(SegmentRegister.ES, cpu)
    }

    private fun load(segmentRegister: SegmentRegister, cpu: CPU) = with(cpu) {
        val addressingMode = AddressingMode.getForCurrentCodeOffset(cpu)
        val address = when (addressingMode) {
            is AddressingMode.Register -> error("Register cannot be used in LDS")
            is AddressingMode.Memory -> memory.getDoubleWord(addressingMode.address)
        }
        val dest = Register16.get(addressingMode.reg)
        registers.set(dest, address.lowerWord)
        registers.set(segmentRegister, address.higherWord)
        registers.ip += addressingMode.byteCount
    }

}