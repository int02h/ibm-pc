package com.dpforge.ibmpc.cpu

class FlagsRegister {

    private var data = RESERVED_BITS

    var value16: Int
        get() = data
        set(value) {
            data = (value and RESERVED_BITS_MASK.inv()) or RESERVED_BITS
        }

    fun getFlag(mask: Int): Boolean {
        return data and mask > 0
    }

    fun setFlag(mask: Int, value: Boolean) {
        data = if (value) {
            data or mask
        } else {
            data and mask.inv()
        }
    }

    override fun toString(): String = buildString {
        append(if (data and CARRY_FLAG > 0) "C" else "_")
        append("_") // reserved
        append(if (data and PARITY_FLAG > 0) "P" else "_")
        append("_") // reserved
        append(if (data and ADJUST_FLAG > 0) "A" else "_")
        append("_") // reserved
        append(if (data and ZERO_FLAG > 0) "Z" else "_")
        append(if (data and SIGN_FLAG > 0) "S" else "_")
        append(if (data and TRAP_FLAG > 0) "T" else "_")
        append(if (data and INTERRUPT_ENABLED_FLAG > 0) "I" else "_")
        append(if (data and DIRECTION_FLAG > 0) "D" else "_")
        append(if (data and OVERFLOW_FLAG > 0) "O" else "_")
    }

    companion object {
        const val CARRY_FLAG = 0x0001
        const val PARITY_FLAG = 0x0004
        const val ADJUST_FLAG = 0x0010
        const val ZERO_FLAG = 0x0040
        const val SIGN_FLAG = 0x0080
        const val TRAP_FLAG = 0x0100
        const val INTERRUPT_ENABLED_FLAG = 0x0200
        const val DIRECTION_FLAG = 0x0400
        const val OVERFLOW_FLAG = 0x0800

        const val RESERVED_BITS = 0b0000_0000_0000_0010
        const val RESERVED_BITS_MASK = 0b1111_0000_0010_1010
    }
}