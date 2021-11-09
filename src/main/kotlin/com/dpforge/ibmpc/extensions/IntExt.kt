package com.dpforge.ibmpc.extensions

val Int.lowerWord: Int
    get() = this and 0xFFFF

val Int.higherWord: Int
    get() = (this shr 16) and 0xFFFF

val Int.lowNibble: Int
    get() = this and 0xF

val Int.highNibble: Int
    get() = (this shr 4) and 0xF

fun Int.bit(index: Int): Boolean = (this shr index) and 0b1 > 0

fun Int.withBit(index: Int, bit: Boolean): Int {
    if (bit) {
        return this or (1 shl index)
    }
    return this and (1 shl index).inv()
}

fun Int.withBitInt(index: Int, bit: Int): Int {
    return withBit(index, bit != 0)
}

fun Int.bitInt(index: Int): Int = (this shr index) and 0b1

fun Int.signExtend8to16(): Int {
    if (this and 0x80 > 0) { // negative value
        return 0xFF00 or (this and 0xFF)
    }
    return this and 0xFF
}

fun Int.signExtend16to32(): Int {
    if (this and 0x8000 > 0) { // negative value
        return 0xFFFF0000.toInt() or (this and 0xFFFF)
    }
    return this and 0xFFFF
}

fun Int.bcd8(): Int {
    if (this shr 8 > 0) {
        error("Out of BCD8 range")
    }
    val highDigit = this / 10
    val lowDigit = this % 10
    return (highDigit shl 4) or lowDigit
}

fun Int.bcd16(): Int {
    if (this shr 16 > 0) {
        error("Out of BCD16 range")
    }
    val d3 = this / 1000
    val d2 = (this - d3 * 1000) / 100
    val d1 = (this - d3 * 1000 - d2 * 100) / 10
    val d0 = this % 10
    return (d3 shl 12) or (d2 shl 8) or (d1 shl 4) or d0
}

fun Int.ensureBitSet(index: Int, errorMessageProvider: () -> String) {
    if (!bit(index)) {
        error(errorMessageProvider())
    }
}

fun Int.ensureBitReset(index: Int, errorMessageProvider: () -> String) {
    if (bit(index)) {
        error(errorMessageProvider())
    }
}