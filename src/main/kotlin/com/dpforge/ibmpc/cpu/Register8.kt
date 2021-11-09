package com.dpforge.ibmpc.cpu

enum class Register8 {
    AL, CL, DL, BL, AH, CH, DH, BH;

    companion object {
        fun get(index: Int): Register8 = values()[index]
    }
}