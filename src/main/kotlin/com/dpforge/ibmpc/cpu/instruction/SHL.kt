package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.timing.Timing
import com.dpforge.ibmpc.cpu.timing.toBitShiftType1
import com.dpforge.ibmpc.cpu.timing.toBitShiftTypeCL

object SHL {

    fun rmb1(cpu: CPU): Int = Timing.shiftBits(
        cpu.rmb { op -> cpu.alu.shl8(op, 1) }
            .toBitShiftType1()
    )

    fun rmb(cpu: CPU, count: Int): Int = Timing.shiftBits(
        cpu.rmb { op -> cpu.alu.shl8(op, count) }
            .toBitShiftTypeCL(count)
    )

    fun rmw1(cpu: CPU): Int = Timing.shiftBits(
        cpu.rmw { op -> cpu.alu.shl16(op, 1) }
            .toBitShiftType1()
    )

    fun rmw(cpu: CPU, count: Int): Int = Timing.shiftBits(
        cpu.rmw { op -> cpu.alu.shl16(op, count) }
            .toBitShiftTypeCL(count)
    )

}
