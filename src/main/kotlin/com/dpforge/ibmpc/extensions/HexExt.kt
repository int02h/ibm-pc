package com.dpforge.ibmpc.extensions

fun Int.toHex(prefix: String = "0x") = prefix + toString(16).padStart(2, '0')

fun Int.toHex2(prefix: String = "0x") = prefix + toString(16).padStart(4, '0')

fun Int.toBinary(prefix: String = "0b") = prefix + toString(2)