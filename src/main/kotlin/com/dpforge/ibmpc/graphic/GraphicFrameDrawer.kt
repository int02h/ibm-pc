package com.dpforge.ibmpc.graphic

import com.dpforge.ibmpc.memory.VideoRAM
import java.awt.Dimension
import java.awt.Graphics

class GraphicFrameDrawer(
    private val displaySize: Dimension, // TODO support scaling
    private val mode: Mode,
    private val cga: CGA,
    private val videoRAM: VideoRAM
) : FrameDrawer {

    private val paletteColors = mapOf(
        CGA.Palette.GREEN_RED_BROWN to arrayOf(CGA.Color.BLACK, CGA.Color.GREEN, CGA.Color.RED, CGA.Color.BROWN),
        CGA.Palette.CYAN_MAGENTA_WHITE to arrayOf(CGA.Color.BLACK, CGA.Color.CYAN, CGA.Color.MAGENTA, CGA.Color.WHITE),
    )

    override fun drawFrame(g: Graphics) {
        for (y in 0 until mode.height) {
            val evenLine = y % 2 == 0
            val baseAddress = if (evenLine) EVEN_LINE_BASE_MEMORY_ADDRESS else ODD_LINE_BASE_MEMORY_ADDRESS
            for (x in 0 until mode.width) {
                val address = baseAddress + (y / 2) * (mode.width / 4) + (x shr 2)
                val b = videoRAM.getByte(address)
                val index = 6 - 2 * (x % 4)
                val pixel = (b shr index) and 0b11
                drawPixel(g, x, y, pixel)
            }
        }
    }

    private fun drawPixel(g: Graphics, x: Int, y: Int, pixel: Int) {
        // TODO support intense colors
        g.color = paletteColors.getValue(cga.palette)[pixel].toAwtColor()
        g.fillRect(x, y, 1, 1) // TODO support scaling
    }

    enum class Mode(val width: Int, val height: Int) {
        COLOR_320x200(width = 320, height = 200),
        BW_320x200(width = 320, height = 200),
        BW_640x200(width = 640, height = 200),
    }

    companion object {
        const val EVEN_LINE_BASE_MEMORY_ADDRESS = 0x18000
        const val ODD_LINE_BASE_MEMORY_ADDRESS = 0x18000 + 0x2000
    }
}