package com.dpforge.ibmpc.cpu

enum class Register16 {
    AX, CX, DX, BX, SP, BP, SI, DI;

    companion object {
        fun get(index: Int): Register16 = values()[index]
    }
}