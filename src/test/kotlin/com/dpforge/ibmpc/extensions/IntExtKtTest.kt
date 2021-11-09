package com.dpforge.ibmpc.extensions

import org.hamcrest.core.IsEqual.equalTo
import org.junit.Test

import org.junit.Assert.*

class IntExtKtTest {

    @Test
    fun bcd8() {
        assertThat(10.bcd8(), equalTo(0b0001_0000))
        assertThat(37.bcd8(), equalTo(0b0011_0111))
        assertThat(49.bcd8(), equalTo(0b0100_1001))
    }

    @Test
    fun bcd16() {
        assertThat(1234.bcd16(), equalTo(0b0001_0010_0011_0100))
    }

    @Test
    fun withBit() {
        assertThat(152.withBit(5, true), equalTo(184))
        assertThat(184.withBit(5, false), equalTo(152))
    }
}