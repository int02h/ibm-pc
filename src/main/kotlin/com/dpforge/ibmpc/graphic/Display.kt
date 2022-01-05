package com.dpforge.ibmpc.graphic

import com.dpforge.ibmpc.memory.VideoRAM
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.event.KeyListener
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

    private val displaySize = Dimension(640, 400)
    private val logger = LoggerFactory.getLogger("DISPLAY")

    private var lastMode: CGA.Mode? = null
    private val frame = JFrame()

    private lateinit var frameDrawer: FrameDrawer

    init {
        preferredSize = displaySize

        frame.add(this)
        frame.addKeyListener(keyListener)
        frame.pack()
        frame.isResizable = false
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

        frameDrawer.drawFrame(g)
    }

    private fun onModeChanged(mode: CGA.Mode) {
        frameDrawer = when (mode) {
            CGA.Mode.TEXT_80x25 -> TextFrameDrawer(displaySize, TextFrameDrawer.Mode.MODE_80x25, cga, videoRAM)
            CGA.Mode.TEXT_40x25 -> TextFrameDrawer(displaySize, TextFrameDrawer.Mode.MODE_40x25, cga, videoRAM)
            CGA.Mode.GRAPHICS_COLOR_320x200 ->
                GraphicFrameDrawer(displaySize, GraphicFrameDrawer.Mode.COLOR_320x200, cga, videoRAM)
            CGA.Mode.GRAPHICS_BW_320x200 -> error("Mode is not supported yet: $mode")
            CGA.Mode.GRAPHICS_BW_640x200 -> error("Mode is not supported yet: $mode")
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
