package com.dpforge.ibmpc

import com.dpforge.ibmpc.port.Port
import com.dpforge.ibmpc.port.PortDevice
import com.dpforge.ibmpc.utils.assertByteArrayEqual
import java.io.File
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class CompiledTests(private val testName: String) {

    @Test
    fun test() {
        executeAndCheck(name = testName, portDevices = listOf(FakePortDevice(port = 0xF100)))
    }

    private fun executeAndCheck(name: String, portDevices: List<PortDevice> = emptyList()) {
        val testPC = TestPC(portDevices = portDevices)
        testPC.execute("80186_tests/$name.bin")

        val expected = getResource("80186_tests/res_$name.bin")
        val actual = testPC.conventionalMemory.get(0, expected.size)
        assertByteArrayEqual(actual = actual, expected = expected)
    }

    private fun getResource(name: String): ByteArray =
        this::class.java.classLoader.getResourceAsStream(name)?.readBytes()
            ?: error("Cannot read resource $name")

    private class FakePortDevice(val port: Int) : PortDevice {
        override fun getPortMapping(): Map<Int, Port> = mapOf(port to FakePort())
    }

    class FakePort : Port {
        override fun write(value: Int) = Unit
        override fun read(): Int = 0
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun getTestNames(): List<String> {
            val url = CompiledTests::class.java.classLoader.getResource("80186_tests")
            val folder = File(url.toURI())
            val tests = folder.listFiles { _, name -> name.endsWith(".bin") && !name.startsWith("res_") }
            return tests.map { it.nameWithoutExtension }
        }

    }
}
