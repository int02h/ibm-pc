package com.dpforge.ibmpc

import com.dpforge.ibmpc.cpu.Registers
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.*
import org.junit.Test

class RegistersTest {

    private val registers = Registers()

    @Test
    fun `al to ax`() {
        registers.al = 56
        assertThat(registers.ax, equalTo(56))
    }

    @Test
    fun `ax to al`() {
        registers.ax = 3969
        assertThat(registers.al, equalTo(129))
    }

    @Test
    fun `ah to ax`() {
        registers.ah = 56
        assertThat(registers.ax, equalTo(14336))
    }

    @Test
    fun `ax to ah`() {
        registers.ax = 3969
        assertThat(registers.ah, equalTo(15))
    }
}