package com.dpforge.ibmpc.graphic

import com.dpforge.ibmpc.extensions.highNibble
import com.dpforge.ibmpc.extensions.lowNibble
import com.dpforge.ibmpc.memory.VideoRAM
import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.FontMetrics
import java.awt.Graphics
import java.awt.event.KeyListener
import java.awt.geom.AffineTransform
import java.util.Timer
import java.util.TimerTask
import javax.swing.JFrame
import javax.swing.JPanel
import kotlin.system.exitProcess
import org.slf4j.LoggerFactory

class Display(
    private val videoRAM: VideoRAM,
    private val cga: CGA,
    keyListener: KeyListener,
) : JPanel() {

    private val logger = LoggerFactory.getLogger("DISPLAY")

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
        frame.addKeyListener(keyListener)
        frame.pack()
        frame.isVisible = true
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                repaint()
            }
        }, 0, 1000 / 60L) // Refresh at a 60 FPS rate.
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        try {
            drawFrame(g)
        } catch (t: Throwable) {
            logger.error("Fail to draw frame", t)
            exitProcess(1)
        }
    }

    private fun drawFrame(g: Graphics) {
        if (lastMode != cga.mode) {
            onModeChanged(cga.mode)
            lastMode = cga.mode
        }

        val fontMetrics = fontMetrics ?: g.fontMetrics.also { fontMetrics = it }

        val cursorRow = cga.cursorAddress / colCount
        val cursorCol = cga.cursorAddress % colCount

        for (row in 0 until rowCount) {
            for (col in 0 until colCount) {
                val cellIndex = (row * (colCount * 2)) + col * 2
                val character = videoRAM.getByte(BASE_MEMORY_ADDRESS + cellIndex).toChar()
                val attribute = videoRAM.getByte(BASE_MEMORY_ADDRESS + cellIndex + 1)

                // draw cell background
                g.color = COLORS[attribute.highNibble]
                g.fillRect(col * cellWidth, row * cellHeight, cellWidth, cellHeight)

                // draw cell foreground
                g.color = COLORS[attribute.lowNibble]

                val cursorTime = System.currentTimeMillis() % 1000 >= 500
                if (cga.isCursorEnabled && cursorTime && row == cursorRow && col == cursorCol) {
                    g.drawString("_", col * cellWidth, row * cellHeight + fontMetrics.ascent)
                } else {
                    g.drawString(character.toString(), col * cellWidth, row * cellHeight + fontMetrics.ascent)
                }
            }
        }
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
                .deriveFont(AffineTransform.getScaleInstance(2.0, 1.0))
        } catch (e: Exception) {
            error("Fail to load custom font: $e")
        }
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
