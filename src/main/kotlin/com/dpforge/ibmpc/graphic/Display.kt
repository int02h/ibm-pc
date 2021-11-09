package com.dpforge.ibmpc.graphic

import com.dpforge.ibmpc.PPI
import com.dpforge.ibmpc.extensions.highNibble
import com.dpforge.ibmpc.extensions.lowNibble
import com.dpforge.ibmpc.memory.VideoRAM
import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.FontMetrics
import java.awt.Graphics
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.util.Timer
import java.util.TimerTask
import javax.swing.JFrame
import javax.swing.JPanel


class Display(
    private val videoRAM: VideoRAM,
    private val cga: CGA,
    private val ppi: PPI,
) : JPanel(), KeyListener {

    private var lastMode: CGA.Mode = cga.mode
    private var rowCount = 0
    private var colCount = 0
    private var cellWidth = 0
    private var cellHeight = 0

    private var fontMetrics: FontMetrics? = null

    init {
        onModeChanged(cga.mode)
        val frame = JFrame()
        frame.add(this)
        frame.addKeyListener(this)
        frame.pack()
        frame.isVisible = true
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                repaint()
            }
        }, 0, 1000 / 60L) // Refresh at a 60 FPS rate.

    }

    override fun keyTyped(e: KeyEvent?) {

    }

    override fun keyPressed(e: KeyEvent) {
        val scanCode = getScanCode(e.keyCode, e.keyLocation)
        if (scanCode > 0) {
            ppi.onKeyTyped(scanCode)
        }
    }

    override fun keyReleased(e: KeyEvent) {
        val scanCode = getScanCode(e.keyCode, e.keyLocation)
        if (scanCode > 0) {
            ppi.onKeyTyped(0x80 or scanCode)
        }
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        if (lastMode != cga.mode) {
            onModeChanged(cga.mode)
            lastMode = cga.mode
        }

        val fontMetrics = fontMetrics ?: g.fontMetrics.also { fontMetrics = it }

        cga.onEndVerticalRetrace()

        for (row in 0 until rowCount) {

            cga.onEndHorizontalRetrace()

            for (col in 0 until colCount) {
                val cellIndex = (row * (colCount * 2)) + col * 2
                val character = videoRAM.getByte(BASE_MEMORY_ADDRESS + cellIndex).toChar()
                val attribute = videoRAM.getByte(BASE_MEMORY_ADDRESS + cellIndex + 1)

                // draw cell background
                g.color =COLORS[attribute.highNibble]
                g.fillRect(col * cellWidth, row * cellHeight, cellWidth, cellHeight)

                // draw cell foreground
                g.color = COLORS[attribute.lowNibble]
                g.drawString(character.toString(), col * cellWidth, row * cellHeight + fontMetrics.ascent)
            }

            cga.onStartHorizontalRetrace()
        }

        cga.onStartVerticalRetrace()
    }

    private fun onModeChanged(mode: CGA.Mode) {
        when (mode) {
            CGA.Mode.TEXT_80x25 -> {
                colCount = 80
                rowCount = 25
                preferredSize = Dimension(640, 200)
            }
            CGA.Mode.TEXT_40x25 -> {
                colCount = 40
                rowCount = 25
                preferredSize = Dimension(320, 200)
            }
            else -> error("Mode is not supported yet: $mode")
        }

        preferredSize = Dimension(2 * preferredSize.width, 2 * preferredSize.height)
        size = preferredSize

        cellWidth = preferredSize.width / colCount
        cellHeight = preferredSize.height / rowCount

        try {
            // Use CP437 TrueType font.
            font = Font.createFont(Font.TRUETYPE_FONT, javaClass.classLoader.getResourceAsStream("cp437.ttf"))
                .deriveFont(cellHeight.toFloat())
        } catch (e: Exception) {
            error("Fail to load custom font: $e")
        }
    }

    private fun getScanCode(keyCode: Int, keyLocation: Int): Int {
        when (keyCode) {
            KeyEvent.VK_ESCAPE -> return 0x01
            KeyEvent.VK_1 -> return 0x02
            KeyEvent.VK_2 -> return 0x03
            KeyEvent.VK_3 -> return 0x04
            KeyEvent.VK_4 -> return 0x05
            KeyEvent.VK_5 -> return 0x06
            KeyEvent.VK_6 -> return 0x07
            KeyEvent.VK_7 -> return 0x08
            KeyEvent.VK_8 -> return 0x09
            KeyEvent.VK_9 -> return 0x0a
            KeyEvent.VK_0 -> return 0x0b
            KeyEvent.VK_MINUS -> {
                if (keyLocation == KeyEvent.KEY_LOCATION_STANDARD) return 0x0c
                if (keyLocation == KeyEvent.KEY_LOCATION_NUMPAD) return 0x4a
            }
            KeyEvent.VK_EQUALS -> return 0x0d
            KeyEvent.VK_BACK_SPACE -> return 0x0e
            KeyEvent.VK_TAB -> return 0x0f
            KeyEvent.VK_Q -> return 0x10
            KeyEvent.VK_W -> return 0x11
            KeyEvent.VK_E -> return 0x12
            KeyEvent.VK_R -> return 0x13
            KeyEvent.VK_T -> return 0x14
            KeyEvent.VK_Y -> return 0x15
            KeyEvent.VK_U -> return 0x16
            KeyEvent.VK_I -> return 0x17
            KeyEvent.VK_O -> return 0x18
            KeyEvent.VK_P -> return 0x19
            KeyEvent.VK_OPEN_BRACKET -> return 0x1a
            KeyEvent.VK_CLOSE_BRACKET -> return 0x1b
            KeyEvent.VK_ENTER -> return 0x1c
            KeyEvent.VK_CONTROL -> return 0x1d
            KeyEvent.VK_A -> return 0x1e
            KeyEvent.VK_S -> return 0x1f
            KeyEvent.VK_D -> return 0x20
            KeyEvent.VK_F -> return 0x21
            KeyEvent.VK_G -> return 0x22
            KeyEvent.VK_H -> return 0x23
            KeyEvent.VK_J -> return 0x24
            KeyEvent.VK_K -> return 0x25
            KeyEvent.VK_L -> return 0x26
            KeyEvent.VK_SEMICOLON -> return 0x27
            KeyEvent.VK_QUOTE -> return 0x28
            KeyEvent.VK_BACK_QUOTE -> return 0x29
            KeyEvent.VK_SHIFT -> {
                if (keyLocation == KeyEvent.KEY_LOCATION_LEFT) return 0x2a
                if (keyLocation == KeyEvent.KEY_LOCATION_RIGHT) return 0x36
            }
            KeyEvent.VK_BACK_SLASH -> return 0x2b
            KeyEvent.VK_Z -> return 0x2c
            KeyEvent.VK_X -> return 0x2d
            KeyEvent.VK_C -> return 0x2e
            KeyEvent.VK_V -> return 0x2f
            KeyEvent.VK_B -> return 0x30
            KeyEvent.VK_N -> return 0x31
            KeyEvent.VK_M -> return 0x32
            KeyEvent.VK_COMMA -> return 0x33
            KeyEvent.VK_PERIOD -> return 0x34
            KeyEvent.VK_SLASH -> return 0x35
            KeyEvent.VK_PRINTSCREEN -> return 0x37
            KeyEvent.VK_ALT -> return 0x38
            KeyEvent.VK_SPACE -> return 0x39
            KeyEvent.VK_CAPS_LOCK -> return 0x3a
            KeyEvent.VK_F1 -> return 0x3b
            KeyEvent.VK_F2 -> return 0x3c
            KeyEvent.VK_F3 -> return 0x3d
            KeyEvent.VK_F4 -> return 0x3e
            KeyEvent.VK_F5 -> return 0x3f
            KeyEvent.VK_F6 -> return 0x40
            KeyEvent.VK_F7 -> return 0x41
            KeyEvent.VK_F8 -> return 0x42
            KeyEvent.VK_F9 -> return 0x43
            KeyEvent.VK_F10 -> return 0x44
            KeyEvent.VK_NUM_LOCK -> return 0x45
            KeyEvent.VK_SCROLL_LOCK -> return 0x46
            KeyEvent.VK_HOME -> return 0x47
            KeyEvent.VK_UP -> return 0x48
            KeyEvent.VK_PAGE_UP -> return 0x49
            KeyEvent.VK_LEFT -> return 0x4b
            KeyEvent.VK_RIGHT -> return 0x4d
            KeyEvent.VK_PLUS -> return 0x4e
            KeyEvent.VK_END -> return 0x4f
            KeyEvent.VK_DOWN -> return 0x50
            KeyEvent.VK_PAGE_DOWN -> return 0x51
            KeyEvent.VK_INSERT -> return 0x52
            KeyEvent.VK_DELETE -> return 0x53
        }
        return 0x00
    }

    companion object {
        const val BASE_MEMORY_ADDRESS = 0x18000

        val COLORS = arrayOf(
            Color(0x00, 0x00, 0x00),
            Color(0x00, 0x00, 0xAA),
            Color(0x00, 0xAA, 0x00),
            Color(0x00, 0xAA, 0xAA),
            Color(0xAA, 0x00, 0x00),
            Color(0xAA, 0x00, 0xAA),
            Color(0xAA, 0x55, 0x00),
            Color(0xAA, 0xAA, 0xAA),
            Color(0x55, 0x55, 0x55),
            Color(0x55, 0x55, 0xFF),
            Color(0x55, 0xFF, 0xFF),
            Color(0x55, 0xFF, 0xFF),
            Color(0xFF, 0x55, 0x55),
            Color(0xFF, 0x55, 0xFF),
            Color(0xFF, 0xFF, 0x55),
            Color(0xFF, 0xFF, 0xFF),
        )
    }
}
