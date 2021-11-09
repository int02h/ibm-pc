package com.dpforge.ibmpc.utils

import com.dpforge.ibmpc.extensions.toHex
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Assert.fail

internal fun assertByteArrayEqual(actual: ByteArray, expected: ByteArray) {
    assertThat(actual.size, equalTo(expected.size))
    repeat(expected.size) { i ->
        val actualByte = actual[i].toInt() and 0xFF
        val expectedByte = expected[i].toInt() and 0xFF
        if (actualByte != expectedByte) {
            fail(
                "Expected $expectedByte (${expectedByte.toHex()}) " +
                        "but $actualByte (${actualByte.toHex()}) was at index $i (${i.toHex()})"
            )
        }
    }
}