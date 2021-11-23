package com.dpforge.ibmpc.cpu.instruction

import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.cpu.timing.Timing
import com.dpforge.ibmpc.cpu.timing.toBitShiftType1
import com.dpforge.ibmpc.cpu.timing.toBitShiftTypeCL

object RCL {

    fun rmb1(cpu: CPU): Int = Timing.rotateBits(
        cpu.rmb { op -> cpu.alu.rcl8(op, 1) }
            .toBitShiftType1()
    )

    fun rmb(cpu: CPU, count: Int): Int = Timing.rotateBits(
        cpu.rmb { op -> cpu.alu.rcl8(op, count) }
            .toBitShiftTypeCL(count)
    )

    fun rmw1(cpu: CPU): Int = Timing.rotateBits(
        cpu.rmw { op -> cpu.alu.rcl16(op, 1) }
            .toBitShiftType1()
    )

    fun rmw(cpu: CPU, count: Int): Int = Timing.rotateBits(
        cpu.rmw { op -> cpu.alu.rcl16(op, count) }
            .toBitShiftTypeCL(count)
    )

}
