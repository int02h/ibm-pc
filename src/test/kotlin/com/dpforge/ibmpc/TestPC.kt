package com.dpforge.ibmpc

import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.memory.BiosROM
import com.dpforge.ibmpc.memory.ConventionalMemory
import com.dpforge.ibmpc.memory.Memory
import com.dpforge.ibmpc.memory.VideoRAM
import com.dpforge.ibmpc.port.PortDevice
import com.dpforge.ibmpc.port.Ports

class TestPC(
    private val portDevices: List<PortDevice> = emptyList()
) {

    val conventionalMemory = ConventionalMemory()

    fun execute(romName: String) {
        val memory = Memory(
            conventionalMemory,
            VideoRAM(),
            BiosROM(getResource(romName))
        )

        val ports = Ports()
        portDevices.forEach { ports.connect(it) }

        val pic = PIC()
        val cpu = CPU(
            memory = memory,
            ports = ports,
            pic = pic,
            pit = PIT(pic),
        )
        cpu.reset()

        while (!cpu.haltState) {
            try {
                cpu.executeNext()
            } catch (error: Throwable) {
                throw error
            }
        }
    }

    private fun getResource(name: String): ByteArray =
        this::class.java.classLoader.getResourceAsStream(name)?.readBytes()
            ?: error("Cannot read resource $name")

}