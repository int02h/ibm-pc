package com.dpforge.ibmpc

import com.dpforge.ibmpc.extensions.bitInt
import com.dpforge.ibmpc.extensions.exhaustive
import com.dpforge.ibmpc.port.Port
import com.dpforge.ibmpc.port.PortDevice
import org.slf4j.LoggerFactory

/**
 * Programmable Interval Timer, Intel 8253
 * https://stanislavs.org/helppc/8253.html
 */
class PIT(
    private val pic: PIC,
) : PortDevice {

    private val logger = LoggerFactory.getLogger("PIT")

    private val counters = Array<Counter?>(3) { null }

    fun update() {
        counters.forEachIndexed { index, counter ->
            val outputRaised = counter?.update()
            if (index == 0 && outputRaised == true) {
                pic.onHardwareInterrupt(0)
            }
        }
    }

    override fun getPortMapping(): Map<Int, Port> = mapOf(
        0x40 to TimeOfDayClock(),
        0x41 to RAMRefreshCounter(),
        0x42 to CassetteAndSpeakerFunctions(),
        0x43 to ControlRegister(),
    )

    inner class TimeOfDayClock : Port {

        override fun write(value: Int) {
            val counter = counters[0] ?: error("Counter 1 is not initialized")
            counter.reset(value)
        }

        override fun read(): Int {
            TODO("Not yet implemented")
        }

    }

    inner class RAMRefreshCounter : Port {

        override fun write(value: Int) {
            val counter = counters[1] ?: error("Counter 1 is not initialized")
            counter.reset(value)
        }

        override fun read(): Int {
            val counter = counters[1] ?: error("Counter 1 is not initialized")
            return counter.getValueWithLatch()
        }

    }

    inner class CassetteAndSpeakerFunctions : Port {

        override fun write(value: Int) {
            val counter = counters[2] ?: error("Counter 2 is not initialized")
            counter.reset(value)
        }

        override fun read(): Int {
            TODO("Not yet implemented")
        }

    }

    inner class ControlRegister : Port {

        override fun write(value: Int) {
            val counter = Counter(
                type = CounterType.values()[value.bitInt(0)],
                mode = CounterMode.values()[(value shr 1) and 0b111],
                format = ReadWriteLatchFormat.values()[(value shr 4) and 0b11],
            )
            val selectCounter = (value shr 6) and 0b11
            if (selectCounter == 0b11) {
                error("read back command (8254 only, illegal on 8253)")
            }
            counters[selectCounter] = counter
            logger.debug(
                "Initialize counter {} with type = {}, mode = {}, format {}",
                selectCounter, counter.type, counter.mode, counter.format
            )
        }

        override fun read(): Int {
            TODO("Not yet implemented")
        }

    }

    private class Counter(
        val type: CounterType,
        val mode: CounterMode,
        val format: ReadWriteLatchFormat
    ) {

        private var output: Boolean = getInitialOutputValue(mode)

        private var resetLSB: Boolean = true

        private var currentValue: Int = 0
            get() = field and 0xFFFF
            set(value) {
                field = value and 0xFFFF
            }

        private var initialValue: Int = 0
            get() = field and 0xFFFF
            set(value) {
                field = value and 0xFFFF
            }

        fun getValueWithLatch(): Int = when (format) {
            ReadWriteLatchFormat.LATCH_PRESENT_COUNTER_VALUE -> currentValue
            ReadWriteLatchFormat.READ_WRITE_OF_MSB_ONLY -> TODO()
            ReadWriteLatchFormat.READ_WRITE_OF_LSB_ONLY -> TODO()
            ReadWriteLatchFormat.READ_WRITE_LSB_FOLLOWED_BY_WRITE_OF_MSB -> TODO()
        }

        fun reset(newValue: Int) {
            when (format) {
                ReadWriteLatchFormat.LATCH_PRESENT_COUNTER_VALUE -> TODO()
                ReadWriteLatchFormat.READ_WRITE_OF_MSB_ONLY -> {
                    initialValue = ((newValue shl 8) or (initialValue and 0xFF)) and 0xFFFF
                }
                ReadWriteLatchFormat.READ_WRITE_OF_LSB_ONLY -> {
                    initialValue = ((newValue and 0xFF) or (initialValue and 0xFF00)) and 0xFFFF
                }
                ReadWriteLatchFormat.READ_WRITE_LSB_FOLLOWED_BY_WRITE_OF_MSB -> {
                    if (resetLSB) {
                        initialValue = ((newValue and 0xFF) or (initialValue and 0xFF00)) and 0xFFFF
                        resetLSB = false
                    } else {
                        initialValue = ((newValue shl 8) or (initialValue and 0xFF)) and 0xFFFF
                        resetLSB = true
                    }
                }
            }.exhaustive
            currentValue = initialValue
        }

        @Suppress("LiftReturnOrAssignment")
        fun update(): Boolean {
            val prevOutput = output
            when (mode) {
                CounterMode.INTERRUPT_ON_TERMINAL_COUNT -> {
                    currentValue -= 1
                    if (currentValue == 0) {
                        output = true
                    }
                }
                CounterMode.PROGRAMMABLE_ONE_SHOT -> TODO()
                CounterMode.RATE_GENERATOR -> {
                    currentValue -= 1
                    if (currentValue == 1) {
                        currentValue = initialValue
                        output = false
                    } else {
                        output = true
                    }
                }
                CounterMode.SQUARE_WAVE_RATE_GENERATOR -> {
                    if (currentValue % 2 == 1) {
                        if (output) {
                            currentValue -= 1
                        } else {
                            currentValue -= 3
                        }
                    } else {
                        currentValue -= 2
                    }

                    if (currentValue == 0) {
                        currentValue = initialValue
                        output = !output
                    }
                }
                CounterMode.SOFTWARE_TRIGGERED_STROBE -> TODO()
                CounterMode.HARDWARE_TRIGGERED_STROBE -> TODO()
            }
            return !prevOutput && output
        }

        private fun getInitialOutputValue(mode: CounterMode): Boolean = when (mode) {
            CounterMode.RATE_GENERATOR,
            CounterMode.SQUARE_WAVE_RATE_GENERATOR -> true
            else -> false
        }
    }

    enum class CounterType {
        BINARY,
        BCD
    }

    enum class CounterMode {
        INTERRUPT_ON_TERMINAL_COUNT,
        PROGRAMMABLE_ONE_SHOT,
        RATE_GENERATOR,
        SQUARE_WAVE_RATE_GENERATOR,
        SOFTWARE_TRIGGERED_STROBE,
        HARDWARE_TRIGGERED_STROBE
    }

    enum class ReadWriteLatchFormat {
        LATCH_PRESENT_COUNTER_VALUE,
        READ_WRITE_OF_MSB_ONLY,
        READ_WRITE_OF_LSB_ONLY,
        READ_WRITE_LSB_FOLLOWED_BY_WRITE_OF_MSB
    }
}