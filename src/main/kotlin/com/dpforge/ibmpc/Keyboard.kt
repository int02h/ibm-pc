package com.dpforge.ibmpc

import java.awt.event.KeyEvent
import java.awt.event.KeyListener

class Keyboard(
    private val ppi: PPI,
) : KeyListener {

    override fun keyTyped(e: KeyEvent?) {
        // do nothing
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

    private fun getScanCode(keyCode: Int, keyLocation: Int): Int = when (keyCode) {
        KeyEvent.VK_ESCAPE -> 0x01
        KeyEvent.VK_1 -> 0x02
        KeyEvent.VK_2 -> 0x03
        KeyEvent.VK_3 -> 0x04
        KeyEvent.VK_4 -> 0x05
        KeyEvent.VK_5 -> 0x06
        KeyEvent.VK_6 -> 0x07
        KeyEvent.VK_7 -> 0x08
        KeyEvent.VK_8 -> 0x09
        KeyEvent.VK_9 -> 0x0a
        KeyEvent.VK_0 -> 0x0b
        KeyEvent.VK_MINUS -> when (keyLocation) {
            KeyEvent.KEY_LOCATION_STANDARD -> 0x0c
            KeyEvent.KEY_LOCATION_NUMPAD -> 0x4a
            else -> 0x00
        }
        KeyEvent.VK_EQUALS -> 0x0d
        KeyEvent.VK_BACK_SPACE -> 0x0e
        KeyEvent.VK_TAB -> 0x0f
        KeyEvent.VK_Q -> 0x10
        KeyEvent.VK_W -> 0x11
        KeyEvent.VK_E -> 0x12
        KeyEvent.VK_R -> 0x13
        KeyEvent.VK_T -> 0x14
        KeyEvent.VK_Y -> 0x15
        KeyEvent.VK_U -> 0x16
        KeyEvent.VK_I -> 0x17
        KeyEvent.VK_O -> 0x18
        KeyEvent.VK_P -> 0x19
        KeyEvent.VK_OPEN_BRACKET -> 0x1a
        KeyEvent.VK_CLOSE_BRACKET -> 0x1b
        KeyEvent.VK_ENTER -> 0x1c
        KeyEvent.VK_CONTROL -> 0x1d
        KeyEvent.VK_A -> 0x1e
        KeyEvent.VK_S -> 0x1f
        KeyEvent.VK_D -> 0x20
        KeyEvent.VK_F -> 0x21
        KeyEvent.VK_G -> 0x22
        KeyEvent.VK_H -> 0x23
        KeyEvent.VK_J -> 0x24
        KeyEvent.VK_K -> 0x25
        KeyEvent.VK_L -> 0x26
        KeyEvent.VK_SEMICOLON -> 0x27
        KeyEvent.VK_QUOTE -> 0x28
        KeyEvent.VK_BACK_QUOTE -> 0x29
        KeyEvent.VK_SHIFT -> when (keyLocation) {
            KeyEvent.KEY_LOCATION_LEFT -> 0x2a
            KeyEvent.KEY_LOCATION_RIGHT -> 0x36
            else -> 0x00
        }
        KeyEvent.VK_BACK_SLASH -> 0x2b
        KeyEvent.VK_Z -> 0x2c
        KeyEvent.VK_X -> 0x2d
        KeyEvent.VK_C -> 0x2e
        KeyEvent.VK_V -> 0x2f
        KeyEvent.VK_B -> 0x30
        KeyEvent.VK_N -> 0x31
        KeyEvent.VK_M -> 0x32
        KeyEvent.VK_COMMA -> 0x33
        KeyEvent.VK_PERIOD -> 0x34
        KeyEvent.VK_SLASH -> 0x35
        KeyEvent.VK_PRINTSCREEN -> 0x37
        KeyEvent.VK_ALT -> 0x38
        KeyEvent.VK_SPACE -> 0x39
        KeyEvent.VK_CAPS_LOCK -> 0x3a
        KeyEvent.VK_F1 -> 0x3b
        KeyEvent.VK_F2 -> 0x3c
        KeyEvent.VK_F3 -> 0x3d
        KeyEvent.VK_F4 -> 0x3e
        KeyEvent.VK_F5 -> 0x3f
        KeyEvent.VK_F6 -> 0x40
        KeyEvent.VK_F7 -> 0x41
        KeyEvent.VK_F8 -> 0x42
        KeyEvent.VK_F9 -> 0x43
        KeyEvent.VK_F10 -> 0x44
        KeyEvent.VK_NUM_LOCK -> 0x45
        KeyEvent.VK_SCROLL_LOCK -> 0x46
        KeyEvent.VK_HOME -> 0x47
        KeyEvent.VK_UP -> 0x48
        KeyEvent.VK_PAGE_UP -> 0x49
        KeyEvent.VK_LEFT -> 0x4b
        KeyEvent.VK_RIGHT -> 0x4d
        KeyEvent.VK_PLUS -> 0x4e
        KeyEvent.VK_END -> 0x4f
        KeyEvent.VK_DOWN -> 0x50
        KeyEvent.VK_PAGE_DOWN -> 0x51
        KeyEvent.VK_INSERT -> 0x52
        KeyEvent.VK_DELETE -> 0x53
        else -> 0x00
    }

}