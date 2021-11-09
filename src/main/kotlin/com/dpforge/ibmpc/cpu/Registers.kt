package com.dpforge.ibmpc.cpu

import com.dpforge.ibmpc.extensions.toHex2

class Registers {

    val flags = FlagsRegister()

    // accumulator
    var ax: Int = 0
        get() = field and 0xFFFF
        set(value) {
            field = value and 0xFFFF
        }

    var al: Int
        get() = ax and 0xFF
        set(value) {
            ax = (ax and 0xFF00) or (value and 0xFF)
        }

    var ah: Int
        get() = (ax and 0xFF00) shr 8
        set(value) {
            ax = (ax and 0x00FF) or ((value and 0xFF) shl 8)
        }

    // base register
    var bx: Int = 0
        get() = field and 0xFFFF
        set(value) {
            field = value and 0xFFFF
        }

    var bl: Int
        get() = bx and 0xFF
        set(value) {
            bx = (bx and 0xFF00) or (value and 0xFF)
        }

    var bh: Int
        get() = (bx and 0xFF00) shr 8
        set(value) {
            bx = (bx and 0x00FF) or ((value and 0xFF) shl 8)
        }

    // counter register
    var cx: Int = 0
        get() = field and 0xFFFF
        set(value) {
            field = value and 0xFFFF
        }

    var cl: Int
        get() = cx and 0xFF
        set(value) {
            cx = (cx and 0xFF00) or (value and 0xFF)
        }

    var ch: Int
        get() = (cx and 0xFF00) shr 8
        set(value) {
            cx = (cx and 0x00FF) or ((value and 0xFF) shl 8)
        }

    // data register
    var dx: Int = 0
        get() = field and 0xFFFF
        set(value) {
            field = value and 0xFFFF
        }

    var dl: Int
        get() = dx and 0xFF
        set(value) {
            dx = (dx and 0xFF00) or (value and 0xFF)
        }

    var dh: Int
        get() = (dx and 0xFF00) shr 8
        set(value) {
            dx = (dx and 0x00FF) or ((value and 0xFF) shl 8)
        }

    // code segment
    var cs: Int = 0
        get() = field and 0xFFFF
        set(value) {
            field = value and 0xFFFF
        }

    // data segment
    var ds: Int = 0
        get() = field and 0xFFFF
        set(value) {
            field = value and 0xFFFF
        }

    // instruction pointer
    var ip: Int = 0
        get() = field and 0xFFFF
        set(value) {
            field = value and 0xFFFF
        }

    // stack segment
    var ss: Int = 0
        get() = field and 0xFFFF
        set(value) {
            field = value and 0xFFFF
        }

    // stack pointer
    var sp: Int = 0
        get() = field and 0xFFFF
        set(value) {
            field = value and 0xFFFF
        }

    // base pointer
    var bp: Int = 0
        get() = field and 0xFFFF
        set(value) {
            field = value and 0xFFFF
        }

    // extra segment
    var es: Int = 0
        get() = field and 0xFFFF
        set(value) {
            field = value and 0xFFFF
        }

    // source index
    var si: Int = 0
        get() = field and 0xFFFF
        set(value) {
            field = value and 0xFFFF
        }

    // destination index
    var di: Int = 0
        get() = field and 0xFFFF
        set(value) {
            field = value and 0xFFFF
        }

    fun get(reg: Register8): Int = when (reg) {
        Register8.AL -> al
        Register8.CL -> cl
        Register8.DL -> dl
        Register8.BL -> bl
        Register8.AH -> ah
        Register8.CH -> ch
        Register8.DH -> dh
        Register8.BH -> bh
    }

    fun set(reg: Register8, value: Int) = when (reg) {
        Register8.AL -> al = value
        Register8.CL -> cl = value
        Register8.DL -> dl = value
        Register8.BL -> bl = value
        Register8.AH -> ah = value
        Register8.CH -> ch = value
        Register8.DH -> dh = value
        Register8.BH -> bh = value
    }

    fun get(reg: Register16): Int = when (reg) {
        Register16.AX -> ax
        Register16.CX -> cx
        Register16.DX -> dx
        Register16.BX -> bx
        Register16.SP -> sp
        Register16.BP -> bp
        Register16.SI -> si
        Register16.DI -> di
    }

    fun set(reg: Register16, value: Int) = when (reg) {
        Register16.AX -> ax = value
        Register16.CX -> cx = value
        Register16.DX -> dx = value
        Register16.BX -> bx = value
        Register16.SP -> sp = value
        Register16.BP -> bp = value
        Register16.SI -> si = value
        Register16.DI -> di = value
    }

    fun get(reg: SegmentRegister): Int = when (reg) {
        SegmentRegister.ES -> es
        SegmentRegister.CS -> cs
        SegmentRegister.SS -> ss
        SegmentRegister.DS -> ds
    }

    fun set(reg: SegmentRegister, value: Int) = when (reg) {
        SegmentRegister.ES -> es = value
        SegmentRegister.CS -> cs = value
        SegmentRegister.SS -> ss = value
        SegmentRegister.DS -> ds = value
    }

    override fun toString(): String = "Registers:" +
            "\nax: ${ax.toHex2()}" +
            "\nbx: ${bx.toHex2()}" +
            "\ncx: ${cx.toHex2()}" +
            "\ndx: ${dx.toHex2()}" +
            "\nds: ${ds.toHex2()}" +
            "\nes: ${es.toHex2()}" +
            "\nss: ${ss.toHex2()}" +
            "\nsp: ${sp.toHex2()}" +
            "\nbp: ${bp.toHex2()}" +
            "\ncs: ${cs.toHex2()}" +
            "\nip: ${ip.toHex2()}" +
            "\nsi: ${si.toHex2()}" +
            "\ndi: ${di.toHex2()}" +
            "\nflags: $flags"
}