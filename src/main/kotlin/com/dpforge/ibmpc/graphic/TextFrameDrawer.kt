package com.dpforge.ibmpc.graphic

import com.dpforge.ibmpc.extensions.highNibble
import com.dpforge.ibmpc.extensions.lowNibble
import com.dpforge.ibmpc.memory.VideoRAM
import java.awt.Dimension
import java.awt.Font
import java.awt.Graphics
import java.awt.geom.AffineTransform

class TextFrameDrawer(
    size: Dimension,
    private val mode: Mode,
    private val cga: CGA,
    private val videoRAM: VideoRAM,
) : FrameDrawer {

    private val font: Font
    private val cellWidth = size.width / mode.colCount
    private val cellHeight = size.height / mode.rowCount

    init {
        try {
            val fontScaleX = size.width.toDouble() / mode.width
            // Use CP437 TrueType font.
            font = Font.createFont(Font.TRUETYPE_FONT, javaClass.classLoader.getResourceAsStream("cp437.ttf"))
                .deriveFont(cellHeight.toFloat())
                .deriveFont(AffineTransform.getScaleInstance(fontScaleX, 1.0))
        } catch (e: Exception) {
            error("Fail to load custom font: $e")
        }
    }

    override fun drawFrame(g: Graphics) {
        g.font = font

        val cursorRow = cga.cursorAddress / mode.colCount
        val cursorCol = cga.cursorAddress % mode.colCount

        for (row in 0 until mode.rowCount) {
            for (col in 0 until mode.colCount) {
                val cellIndex = (row * (mode.colCount * 2)) + col * 2
                val character = videoRAM.getByte(Display.BASE_MEMORY_ADDRESS + cellIndex).toChar()
                val attribute = videoRAM.getByte(Display.BASE_MEMORY_ADDRESS + cellIndex + 1)

                // draw cell background
                g.color = Display.COLORS[attribute.highNibble]
                g.fillRect(col * cellWidth, row * cellHeight, cellWidth, cellHeight)

                // draw cell foreground
                g.color = Display.COLORS[attribute.lowNibble]

                val cursorTime = System.currentTimeMillis() % 1000 >= 500
                if (cga.isCursorEnabled && cursorTime && row == cursorRow && col == cursorCol) {
                    g.drawString("_", col * cellWidth, row * cellHeight + g.fontMetrics.ascent)
                } else {
                    g.drawString(character.toString(), col * cellWidth, row * cellHeight + g.fontMetrics.ascent)
                }
            }
        }
    }

    enum class Mode(val colCount: Int, val rowCount: Int, val width: Int, val height: Int) {
        MODE_80x25(colCount = 80, rowCount = 25, width = 640, height = 200),
        MODE_40x25(colCount = 40, rowCount = 25, width = 320, height = 200);
    }

}