package com.dpforge.ibmpc

import com.dpforge.ibmpc.utils.assertByteArrayEqual
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Test

class MyCompiledTests {

    @Test
    fun addressingMode() {
        val testPC = TestPC()
        testPC.execute("my_tests/addressing_mode.bin")
        val actual = testPC.conventionalMemory.get(0, 40)
        val expected = byteArrayOf(
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x08, 0x00, 0x05, 0x01, 0x06, 0x02, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x07, 0x00, 0x00, 0x00, 0x00, 0x03, 0x00, 0x04,
        )
        assertByteArrayEqual(actual = actual, expected = expected)
    }

    @Test
    fun dma() {
        val testPC = TestPC()
        testPC.execute("my_tests/dma.bin")
        val actual = testPC.conventionalMemory.getByte(0)
        assertThat(actual, equalTo(0xFF))
    }
}