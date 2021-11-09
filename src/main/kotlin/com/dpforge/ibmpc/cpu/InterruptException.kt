package com.dpforge.ibmpc.cpu

class InterruptException(val type: Type) : Exception() {

    enum class Type(val interrupt: Int){
        DIVISION_BY_ZERO(0)
    }

}