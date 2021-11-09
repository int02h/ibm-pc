package com.dpforge.ibmpc

import com.dpforge.ibmpc.cpu.FlagsRegister
import com.dpforge.ibmpc.cpu.FlagsRegister.Companion.ADJUST_FLAG
import com.dpforge.ibmpc.cpu.FlagsRegister.Companion.CARRY_FLAG
import com.dpforge.ibmpc.cpu.FlagsRegister.Companion.OVERFLOW_FLAG
import com.dpforge.ibmpc.cpu.FlagsRegister.Companion.PARITY_FLAG
import com.dpforge.ibmpc.cpu.FlagsRegister.Companion.SIGN_FLAG
import com.dpforge.ibmpc.cpu.FlagsRegister.Companion.ZERO_FLAG
import com.dpforge.ibmpc.cpu.InterruptException
import com.dpforge.ibmpc.extensions.bit
import com.dpforge.ibmpc.extensions.bitInt
import com.dpforge.ibmpc.extensions.higherWord
import com.dpforge.ibmpc.extensions.signExtend16to32
import com.dpforge.ibmpc.extensions.signExtend8to16

class ALU(
    private val flagsRegister: FlagsRegister,
) {

    fun xor8(op1: Int, op2: Int): Int {
        val m1 = op1 and MASK8
        val m2 = op2 and MASK8
        val result = m1 xor m2

        updateLogicalOperationFlags8(result)

        return result and MASK8
    }

    fun xor16(op1: Int, op2: Int): Int {
        val m1 = op1 and MASK16
        val m2 = op2 and MASK16
        val result = m1 xor m2

        updateLogicalOperationFlags16(result)

        return result and MASK16
    }

    fun sub8(op1: Int, op2: Int, useCarry: Boolean = false): Int {
        val m1 = op1 and MASK8
        val m2 = op2 and MASK8
        val result = m1 - m2 - (if (useCarry) getCarryFlagAsInt() else 0)

        updateCarryFlag8(result)
        updateParityFlag(result)
        updateAdjustFlag(m1, m2, result)
        updateZeroFlag8(result)
        updateSignFlag8(result)

        val overflow = (m1.isNegative8() && m2.isPositive8() && result.isPositive8())
                || (m1.isPositive8() && m2.isNegative8() && result.isNegative8())
        flagsRegister.setFlag(OVERFLOW_FLAG, overflow)

        return result and MASK8
    }

    fun sub16(op1: Int, op2: Int, useCarry: Boolean = false): Int {
        val m1 = op1 and MASK16
        val m2 = op2 and MASK16
        val result = m1 - m2 - (if (useCarry) getCarryFlagAsInt() else 0)

        updateCarryFlag16(result)
        updateParityFlag(result)
        updateAdjustFlag(m1, m2, result)
        updateZeroFlag16(result)
        updateSignFlag16(result)

        val overflow = (m1.isNegative16() && m2.isPositive16() && result.isPositive16())
                || (m1.isPositive16() && m2.isNegative16() && result.isNegative16())
        flagsRegister.setFlag(OVERFLOW_FLAG, overflow)

        return result and MASK16
    }

    fun add8(op1: Int, op2: Int, useCarry: Boolean = false): Int {
        val m1 = op1 and MASK8
        val m2 = op2 and MASK8
        val result = m1 + m2 + (if (useCarry) getCarryFlagAsInt() else 0)

        updateCarryFlag8(result)
        updateParityFlag(result)
        updateAdjustFlag(m1, m2, result)
        updateZeroFlag8(result)
        updateSignFlag8(result)

        val overflow = (m1.isNegative8() && m2.isNegative8() && result.isPositive8())
                || (m1.isPositive8() && m2.isPositive8() && result.isNegative8())
        flagsRegister.setFlag(OVERFLOW_FLAG, overflow)

        return result and MASK8
    }

    fun add16(op1: Int, op2: Int, useCarry: Boolean = false): Int {
        val m1 = op1 and MASK16
        val m2 = op2 and MASK16
        val result = m1 + m2 + (if (useCarry) getCarryFlagAsInt() else 0)

        updateCarryFlag16(result)
        updateParityFlag(result)
        updateAdjustFlag(m1, m2, result)
        updateZeroFlag16(result)
        updateSignFlag16(result)

        val overflow = (m1.isNegative16() && m2.isNegative16() && result.isPositive16())
                || (m1.isPositive16() && m2.isPositive16() && result.isNegative16())
        flagsRegister.setFlag(OVERFLOW_FLAG, overflow)

        return result and MASK16
    }

    fun and8(op1: Int, op2: Int): Int {
        val m1 = op1 and MASK8
        val m2 = op2 and MASK8
        val result = m1 and m2

        updateLogicalOperationFlags8(result)

        return result and MASK8
    }

    fun and16(op1: Int, op2: Int): Int {
        val m1 = op1 and MASK16
        val m2 = op2 and MASK16
        val result = m1 and m2

        updateLogicalOperationFlags16(result)

        return result and MASK16
    }

    fun or8(op1: Int, op2: Int): Int {
        val m1 = op1 and MASK8
        val m2 = op2 and MASK8
        val result = m1 or m2

        updateLogicalOperationFlags8(result)

        return result and MASK8
    }

    fun or16(op1: Int, op2: Int): Int {
        val m1 = op1 and MASK16
        val m2 = op2 and MASK16
        val result = m1 or m2

        updateLogicalOperationFlags16(result)

        return result and MASK16
    }

    fun not8(op: Int): Int {
        val m = op and MASK8
        val result = m.inv()
        return result and MASK8
    }

    fun not16(op: Int): Int {
        val m = op and MASK16
        val result = m.inv()
        return result and MASK16
    }

    fun inc8(op1: Int): Int = executeKeepingCarryFlag { add8(op1, 1) }

    fun inc16(op1: Int): Int = executeKeepingCarryFlag { add16(op1, 1) }

    fun dec8(op1: Int): Int = executeKeepingCarryFlag { sub8(op1, 1) }

    fun dec16(op1: Int): Int = executeKeepingCarryFlag { sub16(op1, 1) }

    fun mul8(op1: Int, op2: Int): Int {
        val m1 = op1 and MASK8
        val m2 = op2 and MASK8
        val result = m1 * m2

        flagsRegister.setFlag(CARRY_FLAG, (result and 0xFF00) != 0)
        flagsRegister.setFlag(OVERFLOW_FLAG, (result and 0xFF00) != 0)

        updateZeroFlag8(result)
        updateParityFlag(result)
        updateSignFlag8(result)

        flagsRegister.setFlag(ADJUST_FLAG, false)

        return result and MASK16
    }

    fun mul16(op1: Int, op2: Int): Int {
        val m1 = op1 and MASK16
        val m2 = op2 and MASK16
        val result = m1 * m2

        flagsRegister.setFlag(CARRY_FLAG, result.higherWord != 0)
        flagsRegister.setFlag(OVERFLOW_FLAG, result.higherWord != 0)

        updateZeroFlag16(result)
        updateParityFlag(result)
        updateSignFlag16(result)

        flagsRegister.setFlag(ADJUST_FLAG, false)

        return result
    }

    fun imul8(op1: Int, op2: Int): Int {
        val m1 = (op1 and MASK8).toByte()
        val m2 = (op2 and MASK8).toByte()
        val result = ((m1 * m2) and MASK16).toShort().toInt() and MASK16

        flagsRegister.setFlag(CARRY_FLAG, result != result.signExtend8to16())
        flagsRegister.setFlag(OVERFLOW_FLAG, result != result.signExtend8to16())

        updateZeroFlag8(result)
        updateParityFlag(result)
        updateSignFlag8(result)

        flagsRegister.setFlag(ADJUST_FLAG, false)

        return (result and MASK16).toShort().toInt() and MASK16
    }

    fun imul16(op1: Int, op2: Int): Int {
        val m1 = (op1 and MASK16).toShort()
        val m2 = (op2 and MASK16).toShort()
        val result = m1 * m2

        flagsRegister.setFlag(CARRY_FLAG, result != result.signExtend16to32())
        flagsRegister.setFlag(OVERFLOW_FLAG, result != result.signExtend16to32())

        updateZeroFlag16(result)
        updateParityFlag(result)
        updateSignFlag16(result)

        flagsRegister.setFlag(ADJUST_FLAG, false)

        return result
    }

    fun div32by16(op1: Int, op2: Int): DivResult {
        val m1 = op1.toLong() and MASK32

        val m2 = op2 and MASK16
        if (m2 == 0) {
            throw InterruptException(InterruptException.Type.DIVISION_BY_ZERO)
        }

        val quotientUnmasked = (m1 / m2).toInt()
        if (quotientUnmasked.higherWord != 0) {
            throw InterruptException(InterruptException.Type.DIVISION_BY_ZERO)
        }

        val quotient = quotientUnmasked and MASK16
        val reminder = (m1 % m2).toInt() and MASK16

        flagsRegister.setFlag(CARRY_FLAG, false)
        flagsRegister.setFlag(OVERFLOW_FLAG, false)
        flagsRegister.setFlag(ADJUST_FLAG, false)

        updateZeroFlag16(quotient)
        updateParityFlag(quotient)
        updateSignFlag16(quotient)

        return DivResult(quotient = quotient, reminder = reminder)
    }

    fun div16by8(op1: Int, op2: Int): DivResult {
        val m1 = op1 and MASK16

        val m2 = op2 and MASK8
        if (m2 == 0) {
            throw InterruptException(InterruptException.Type.DIVISION_BY_ZERO)
        }

        val quotientUnmasked = m1 / m2
        if (quotientUnmasked and 0xFF00 != 0) {
            throw InterruptException(InterruptException.Type.DIVISION_BY_ZERO)
        }

        val quotient = quotientUnmasked and MASK8
        val reminder = (m1 % m2) and MASK8

        flagsRegister.setFlag(CARRY_FLAG, false)
        flagsRegister.setFlag(OVERFLOW_FLAG, false)
        flagsRegister.setFlag(ADJUST_FLAG, false)

        updateZeroFlag8(quotient)
        updateParityFlag(quotient)
        updateSignFlag8(quotient)

        return DivResult(quotient = quotient, reminder = reminder)
    }

    fun idiv32by16(op1: Int, op2: Int): DivResult {
        val m1 = op1
        val m2 = (op2 and MASK16).toShort()
        if (m2 == 0.toShort()) {
            throw InterruptException(InterruptException.Type.DIVISION_BY_ZERO)
        }

        val quotientUnmasked = m1 / m2
        if (quotientUnmasked != quotientUnmasked.signExtend16to32()) {
            throw InterruptException(InterruptException.Type.DIVISION_BY_ZERO)
        }

        val quotient = quotientUnmasked and MASK16
        val reminder = (m1 % m2) and MASK16

        flagsRegister.setFlag(CARRY_FLAG, false)
        flagsRegister.setFlag(OVERFLOW_FLAG, false)
        flagsRegister.setFlag(ADJUST_FLAG, false)

        updateZeroFlag16(quotient)
        updateParityFlag(quotient)
        updateSignFlag16(quotient)

        return DivResult(quotient = quotient, reminder = reminder)
    }

    fun idiv16by8(op1: Int, op2: Int): DivResult {
        val m1 = (op1 and MASK16).toShort()

        val m2 = (op2 and MASK8).toByte()
        if (m2 == 0.toByte()) {
            throw InterruptException(InterruptException.Type.DIVISION_BY_ZERO)
        }

        val quotientUnmasked = m1 / m2
        if (quotientUnmasked and MASK16 != quotientUnmasked.signExtend8to16()) {
            throw InterruptException(InterruptException.Type.DIVISION_BY_ZERO)
        }

        val quotient = quotientUnmasked and MASK8
        val reminder = (m1 % m2) and MASK8

        flagsRegister.setFlag(CARRY_FLAG, false)
        flagsRegister.setFlag(OVERFLOW_FLAG, false)
        flagsRegister.setFlag(ADJUST_FLAG, false)

        updateZeroFlag8(quotient)
        updateParityFlag(quotient)
        updateSignFlag8(quotient)

        return DivResult(quotient = quotient, reminder = reminder)
    }

    fun neg8(op: Int): Int {
        val m = op and MASK8
        val result = -op

        flagsRegister.setFlag(CARRY_FLAG, m != 0)
        flagsRegister.setFlag(OVERFLOW_FLAG, m != 0 && (result and MASK8) == m)

        updateSignFlag8(result)
        updateZeroFlag8(result)
        updateAdjustFlag(0, m, result)
        updateParityFlag(result)

        return result and MASK8
    }

    fun neg16(op: Int): Int {
        val m = op and MASK16
        val result = -op

        flagsRegister.setFlag(CARRY_FLAG, m != 0)
        flagsRegister.setFlag(OVERFLOW_FLAG, m != 0 && (result and MASK16) == m)

        updateSignFlag16(result)
        updateZeroFlag16(result)
        updateAdjustFlag(0, m, result)
        updateParityFlag(result)

        return result and MASK16
    }

    fun rol8(op: Int, count: Int): Int {
        var m = op and MASK8

        if (count == 0) {
            return m
        }

        repeat(count) {
            m = m shl 1
            m = m or m.bitInt(8)
            m = (m and MASK8)
        }

        flagsRegister.setFlag(CARRY_FLAG, m.bit(0))
        flagsRegister.setFlag(OVERFLOW_FLAG, m.bit(7) xor flagsRegister.getFlag(CARRY_FLAG))

        return m
    }

    fun rol16(op: Int, count: Int): Int {
        var m = op and MASK16

        if (count == 0) {
            return m
        }

        repeat(count) {
            m = m shl 1
            m = m or m.bitInt(16)
            m = (m and MASK16)
        }

        flagsRegister.setFlag(CARRY_FLAG, m.bit(0))
        flagsRegister.setFlag(OVERFLOW_FLAG, m.bit(15) xor flagsRegister.getFlag(CARRY_FLAG))

        return m
    }

    fun ror8(op: Int, count: Int): Int {
        var m = op and MASK8

        if (count == 0) {
            return m
        }

        repeat(count) {
            val lsb = m.bitInt(0)
            m = m shr 1
            m = m or (lsb shl 7)
            m = (m and MASK8)
        }

        flagsRegister.setFlag(CARRY_FLAG, m.bit(7))
        flagsRegister.setFlag(OVERFLOW_FLAG, m.bit(7) xor m.bit(6))

        return m
    }

    fun ror16(op: Int, count: Int): Int {
        var m = op and MASK16

        if (count == 0) {
            return m
        }

        repeat(count) {
            val lsb = m.bitInt(0)
            m = m shr 1
            m = m or (lsb shl 15)
            m = (m and MASK16)
        }

        flagsRegister.setFlag(CARRY_FLAG, m.bit(15))
        flagsRegister.setFlag(OVERFLOW_FLAG, m.bit(15) xor m.bit(14))

        return m
    }

    fun rcl8(op: Int, count: Int): Int {
        var m = op and MASK8

        if (count == 0) {
            return m
        }

        repeat(count) {
            m = (m shl 1) or getCarryFlagAsInt()
            flagsRegister.setFlag(CARRY_FLAG, m.bit(8))
            m = m and MASK8
        }

        flagsRegister.setFlag(OVERFLOW_FLAG, m.bit(7) xor flagsRegister.getFlag(CARRY_FLAG))

        return m
    }

    fun rcl16(op: Int, count: Int): Int {
        var m = op and MASK16

        if (count == 0) {
            return m
        }

        repeat(count) {
            m = (m shl 1) or getCarryFlagAsInt()
            flagsRegister.setFlag(CARRY_FLAG, m.bit(16))
            m = m and MASK16
        }

        flagsRegister.setFlag(OVERFLOW_FLAG, m.bit(15) xor flagsRegister.getFlag(CARRY_FLAG))

        return m
    }

    fun rcr8(op: Int, count: Int): Int {
        var m = op and MASK8

        if (count == 0) {
            return m
        }

        flagsRegister.setFlag(OVERFLOW_FLAG, m.bit(7) xor flagsRegister.getFlag(CARRY_FLAG))

        repeat(count) {
            val lsb = m.bit(0)
            m = (m shr 1) or (getCarryFlagAsInt() shl 7)
            flagsRegister.setFlag(CARRY_FLAG, lsb)
            m = m and MASK8
        }

        return m

    }

    fun rcr16(op: Int, count: Int): Int {
        var m = op and MASK16

        if (count == 0) {
            return m
        }

        flagsRegister.setFlag(OVERFLOW_FLAG, m.bit(15) xor flagsRegister.getFlag(CARRY_FLAG))

        repeat(count) {
            val lsb = m.bit(0)
            m = (m shr 1) or (getCarryFlagAsInt() shl 15)
            flagsRegister.setFlag(CARRY_FLAG, lsb)
            m = m and MASK16
        }

        return m

    }

    fun shl8(op: Int, count: Int): Int {
        var m = op and MASK8

        if (count == 0) {
            return m
        }

        repeat(count) {
            flagsRegister.setFlag(CARRY_FLAG, m.bit(7))
            m = m shl 1
            m = m and MASK8
        }

        flagsRegister.setFlag(OVERFLOW_FLAG, m.bit(7) xor flagsRegister.getFlag(CARRY_FLAG))

        updateSignFlag8(m)
        updateZeroFlag8(m)
        updateParityFlag(m)
        // AF ?

        return m
    }

    fun shl16(op: Int, count: Int): Int {
        var m = op and MASK16

        if (count == 0) {
            return m
        }

        repeat(count) {
            flagsRegister.setFlag(CARRY_FLAG, m.bit(15))
            m = m shl 1
            m = m and MASK16
        }

        flagsRegister.setFlag(OVERFLOW_FLAG, m.bit(15) xor flagsRegister.getFlag(CARRY_FLAG))

        updateSignFlag16(m)
        updateZeroFlag16(m)
        updateParityFlag(m)
        // AF ?

        return m
    }

    fun sar8(op: Int, count: Int): Int {
        var m = op and MASK8

        if (count == 0) {
            return m
        }

        repeat(count) {
            flagsRegister.setFlag(CARRY_FLAG, m.bit(0))
            val roundNegative = m.isNegative8() && (m % 2 != 0)
            m = m.toByte() / 2
            if (roundNegative) {
                m -= 1
            }
        }

        flagsRegister.setFlag(OVERFLOW_FLAG, false)

        updateSignFlag8(m)
        updateZeroFlag8(m)
        updateParityFlag(m)
        // AF ?

        return m and MASK8
    }

    fun sar16(op: Int, count: Int): Int {
        var m = op and MASK16

        if (count == 0) {
            return m
        }

        repeat(count) {
            flagsRegister.setFlag(CARRY_FLAG, m.bit(0))
            val roundNegative = m.isNegative16() && (m % 2 != 0)
            m = m.toShort() / 2
            if (roundNegative) {
                m -= 1
            }
        }

        flagsRegister.setFlag(OVERFLOW_FLAG, false)

        updateSignFlag16(m)
        updateZeroFlag16(m)
        updateParityFlag(m)
        // AF ?

        return m and MASK16
    }

    fun shr8(op: Int, count: Int): Int {
        var m = op and MASK8

        if (count == 0) {
            return m
        }

        repeat(count) {
            flagsRegister.setFlag(CARRY_FLAG, m.bit(0))
            m /= 2
        }

        flagsRegister.setFlag(OVERFLOW_FLAG, op.bit(7))

        updateSignFlag8(m)
        updateZeroFlag8(m)
        updateParityFlag(m)
        // AF ?

        return m and MASK8
    }

    fun shr16(op: Int, count: Int): Int {
        var m = op and MASK16

        if (count == 0) {
            return m
        }

        repeat(count) {
            flagsRegister.setFlag(CARRY_FLAG, m.bit(0))
            m /= 2
        }

        flagsRegister.setFlag(OVERFLOW_FLAG, op.bit(15))

        updateSignFlag16(m)
        updateZeroFlag16(m)
        updateParityFlag(m)
        // AF ?

        return m and MASK16
    }

    private fun getCarryFlagAsInt(): Int = if (flagsRegister.getFlag(CARRY_FLAG)) 1 else 0

    private fun updateLogicalOperationFlags8(result: Int) {
        flagsRegister.setFlag(OVERFLOW_FLAG, false)
        flagsRegister.setFlag(CARRY_FLAG, false)
        flagsRegister.setFlag(ADJUST_FLAG, false)
        updateSignFlag8(result)
        updateZeroFlag8(result)
        updateParityFlag(result)
    }

    private fun updateLogicalOperationFlags16(result: Int) {
        flagsRegister.setFlag(OVERFLOW_FLAG, false)
        flagsRegister.setFlag(CARRY_FLAG, false)
        flagsRegister.setFlag(ADJUST_FLAG, false)
        updateSignFlag16(result)
        updateZeroFlag16(result)
        updateParityFlag(result)
    }

    private fun updateCarryFlag8(result: Int) {
        flagsRegister.setFlag(CARRY_FLAG, result and BIT9 > 0)
    }

    private fun updateCarryFlag16(result: Int) {
        flagsRegister.setFlag(CARRY_FLAG, result and BIT17 > 0)
    }

    fun updateParityFlag(result: Int) {
        flagsRegister.setFlag(PARITY_FLAG, (result and MASK8).countOneBits() % 2 == 0)
    }

    private fun updateAdjustFlag(op1: Int, op2: Int, result: Int) {
        flagsRegister.setFlag(ADJUST_FLAG, (op2 xor op1 xor result) and 0x10 > 0)
    }

    fun updateZeroFlag8(result: Int) {
        flagsRegister.setFlag(ZERO_FLAG, result and MASK8 == 0)
    }

    private fun updateZeroFlag16(result: Int) {
        flagsRegister.setFlag(ZERO_FLAG, result and MASK16 == 0)
    }

    fun updateSignFlag8(result: Int) {
        flagsRegister.setFlag(SIGN_FLAG, result and BIT8 > 0)
    }

    private fun updateSignFlag16(result: Int) {
        flagsRegister.setFlag(SIGN_FLAG, result and BIT16 > 0)
    }

    private fun Int.isNegative8() = this and BIT8 > 0

    private fun Int.isPositive8() = this and BIT8 == 0

    private fun Int.isNegative16() = this and BIT16 > 0

    private fun Int.isPositive16() = this and BIT16 == 0

    class DivResult(
        val quotient: Int,
        val reminder: Int
    )

    private inline fun executeKeepingCarryFlag(operation: () -> Int): Int {
        val carryFlag = flagsRegister.getFlag(CARRY_FLAG)
        val res = operation()
        flagsRegister.setFlag(CARRY_FLAG, carryFlag)
        return res
    }

    private companion object {
        const val MASK8 = 0xFF
        const val MASK16 = 0xFFFF
        const val MASK32 = 0xFFFF_FFFF

        // TODO rename to -1 value
        const val BIT8 = 0x80
        const val BIT9 = 0x100
        const val BIT16 = 0x8000
        const val BIT17 = 0x10000
    }
}