package com.dpforge.ibmpc.graphic

import com.dpforge.ibmpc.extensions.bit
import com.dpforge.ibmpc.extensions.bitInt
import com.dpforge.ibmpc.extensions.withBit
import com.dpforge.ibmpc.memory.Memory
import com.dpforge.ibmpc.port.Port
import com.dpforge.ibmpc.port.PortDevice
import org.slf4j.LoggerFactory

/**
 * Color Graphics Adapter
 * https://stanislavs.org/helppc/6845.html
 */
class CGA(
    val memory: Memory,
) : PortDevice {

    private val logger = LoggerFactory.getLogger("CGA")

    private var registerIndex = 0
    private val registers = IntArray(0x12) { 0 }

    private var backgroundColor = Color.BLACK

    @Volatile
    private var inHorizontalRetrace = true

    @Volatile
    private var inVerticalRetrace = true

    var mode: Mode = Mode.TEXT_80x25
        private set

    val cursorAddress: Int
        get() = (registers[REGISTER_CURSOR_ADDRESS_MSB] shl 8) or (registers[REGISTER_CURSOR_ADDRESS_LSB])

    val isCursorEnabled: Boolean
        get() = !registers[REGISTER_CURSOR_START].bit(5)

    fun onStartVerticalRetrace() {
        inVerticalRetrace = true
    }

    fun onEndVerticalRetrace() {
        inVerticalRetrace = false
    }

    fun onStartHorizontalRetrace() {
        inHorizontalRetrace = true
    }

    fun onEndHorizontalRetrace() {
        inHorizontalRetrace = false
    }

    override fun getPortMapping(): Map<Int, Port> = mapOf(
        0x3D4 to IndexRegister(),
        0x3D5 to DataRegister(),
        0x3D8 to ModeControlRegister(),
        0x3D9 to ColorRegister(),
        0x3DA to StatusRegister(),
    )

    private inner class IndexRegister : Port {

        override fun write(value: Int) {
            registerIndex = value
        }

        override fun read(): Int = registerIndex

    }

    private inner class DataRegister : Port {

        override fun write(value: Int) {
            if (registerIndex in 0x10..0x11) {
                error("Registers 0x10..0x11 are read only")
            }
            registers[registerIndex] = value

            when (registerIndex) {
                REGISTER_CURSOR_START -> {
                    logger.debug("Cursor enabled: $isCursorEnabled")
                }
            }
        }

        override fun read(): Int {
            if (registerIndex in 0x00..0x0D) {
                error("Registers 0x00..0x0D are write only")
            }
            return registers[registerIndex]
        }

    }

    private inner class ModeControlRegister : Port {

        override fun write(value: Int) {
            mode = if (value.bit(4)) {
                Mode.GRAPHICS_BW_640x200
            } else {
                if (value.bit(1)) {
                    if (value.bit(2)) {
                        Mode.GRAPHICS_BW_320x200
                    } else {
                        Mode.GRAPHICS_COLOR_320x200
                    }
                } else {
                    if (value.bit(0)) {
                        Mode.TEXT_80x25
                    } else {
                        Mode.TEXT_40x25
                    }
                }
            }
            val enableVideoSignal = value.bit(3)
            val blink = value.bit(5)
            logger.debug("Select mode $mode, enable video signal = $enableVideoSignal, blink = $blink")
        }

        override fun read(): Int {
            TODO("Not yet implemented")
        }
    }

    private inner class ColorRegister : Port {

        override fun write(value: Int) {
            backgroundColor = Color.values()[value and 0xF]
            // bit 4 not used
            val palette = Palette.values()[value.bitInt(5)]
            // bit 6-7 not used

            logger.debug("Select color $backgroundColor, palette = $palette")
        }

        override fun read(): Int {
            TODO("Not yet implemented")
        }

    }

    private inner class StatusRegister : Port {

        override fun write(value: Int) {
            TODO("Not yet implemented")
        }

        override fun read(): Int {
            var status = 0
            status = status.withBit(0, inHorizontalRetrace)
            // bit 1 - positive edge from light pen has set trigger
            // bit 2 - light pen switch is off
            status = status.withBit(3, inVerticalRetrace)
            // bit 4-7 not used
            return status
        }

    }

    enum class Mode {
        TEXT_80x25,
        TEXT_40x25,
        GRAPHICS_COLOR_320x200,
        GRAPHICS_BW_320x200,
        GRAPHICS_BW_640x200,
    }

    enum class Color(val r: Int, val g: Int, val b: Int) {
        BLACK(0x00, 0x00, 0x00),
        BLUE(0x00, 0x00, 0xAA),
        GREEN(0x00, 0xAA, 0x00),
        CYAN(0x00, 0xAA, 0xAA),
        RED(0xAA, 0x00, 0x00),
        MAGENTA(0xAA, 0x00, 0xAA),
        BROWN(0xAA, 0x55, 0x00),
        LIGHT_GRAY(0xAA, 0xAA, 0xAA),
        DARK_GRAY(0x55, 0x55, 0x55),
        LIGHT_BLUE(0x55, 0x55, 0xFF),
        LIGHT_GREEN(0x55, 0xFF, 0xFF),
        LIGHT_CYAN(0x55, 0xFF, 0xFF),
        LIGHT_RED(0xFF, 0x55, 0x55),
        LIGHT_MAGENTA(0xFF, 0x55, 0xFF),
        YELLOW(0xFF, 0xFF, 0x55),
        WHITE(0xFF, 0xFF, 0xFF),
    }

    private enum class Palette {
        RED_GREEN_BROWN,
        CYAN_MAGENTA_WHITE
    }

    private companion object {
        const val REGISTER_CURSOR_START = 0x0A
        const val REGISTER_CURSOR_ADDRESS_MSB = 0x0E
        const val REGISTER_CURSOR_ADDRESS_LSB = 0x0F
    }

}