package com.dpforge.ibmpc.cpu

enum class SegmentRegister {
    ES, CS, SS, DS;

    companion object {
        fun get(index: Int): SegmentRegister = values()[index]
    }
}