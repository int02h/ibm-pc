package com.dpforge.ibmpc

import com.dpforge.ibmpc.extensions.bit
import com.dpforge.ibmpc.extensions.bitInt
import com.dpforge.ibmpc.extensions.toHex
import com.dpforge.ibmpc.port.Port
import com.dpforge.ibmpc.port.PortDevice
import com.dpforge.ibmpc.port.WriteOnlyPort
import org.slf4j.LoggerFactory

class DMA : PortDevice {

    private val logger = LoggerFactory.getLogger("DMA")

    private var mode = Mode(0)
    private var singleMask = SingleMask(0)

    private val address = IntArray(4)
    private val wordCount = IntArray(4)

    override fun getPortMapping(): Map<Int, com.dpforge.ibmpc.port.Port> = mapOf(
        0x00 to AddressRegister(channel = 0),
        0x01 to WordCount(channel = 0),
        0x02 to AddressRegister(channel = 1),
        0x03 to WordCount(channel = 1),
        0x04 to AddressRegister(channel = 2),
        0x05 to WordCount(channel = 2),
        0x06 to AddressRegister(channel = 3),
        0x07 to WordCount(channel = 3),
        0x08 to StatusCommandRegister(),
        0x0A to MaskRegister(),
        0x0B to ModeRegister(),
        0x0D to ResetPort(),
        0x81 to PageRegister(2),
        0x82 to PageRegister(3),
        0x83 to PageRegister(1),
        0xD4 to MaskPort(),
        0xD6 to ModePort(),
        0xDA to ClearPort(),
    )

    private inner class AddressRegister(val channel: Int) : Port {

        override fun write(value: Int) {
            address[channel] = value
        }

        override fun read(): Int = address[channel]

    }

    private inner class WordCount(val channel: Int) : Port {

        override fun write(value: Int) {
            wordCount[channel] = value
        }

        override fun read(): Int = wordCount[channel]

    }

    private inner class StatusCommandRegister : Port {

        // command
        override fun write(value: Int) {
            val channelNumber = value and 0b11
            val isControllerEnabled = value.bit(2)
            val timing = Timing.values()[value.bitInt(3)]
            val priority = Priority.values()[value.bitInt(4)]
            val writeSelection = WriteSelection.values()[value.bitInt(5)]
            // bit 6 - DREQ sense active high
            // bit 7 - DACK sense active high
            logger.debug(
                "channel = $channelNumber" +
                        ", controlled enabled = $isControllerEnabled" +
                        ", timing = $timing" +
                        ", priority = $priority" +
                        ", write selection = $writeSelection"
            )
        }

        // stsatus
        override fun read(): Int {
            TODO("Not yet implemented")
        }

    }

    private inner class MaskRegister : Port {

        override fun write(value: Int) {
            val channelNumber = value and 0b11
            val maskBit = value.bit(2)
            val action = if (maskBit) "Set" else "Clear"
            // bits 3-7 reserved (0)
            logger.debug("$action mask bit for channel $channelNumber")
        }

        override fun read(): Int {
            TODO("Not yet implemented")
        }

    }

    private inner class ModeRegister : Port {

        override fun write(value: Int) {
            logger.debug("Set mode ${value.toHex()}")
        }

        override fun read(): Int {
            TODO("Not yet implemented")
        }

    }

    private inner class ResetPort : WriteOnlyPort() {

        override fun write(value: Int) {
            logger.debug("reset")
            clearInternal()
        }

    }

    private inner class PageRegister(val channel: Int) : Port {

        override fun write(value: Int) {
            logger.debug("Set address $value (${value.toHex()}) of channel $channel")
        }

        override fun read(): Int {
            TODO("Not yet implemented")
        }

    }

    private inner class MaskPort : WriteOnlyPort() {

        override fun write(value: Int) {
            singleMask = SingleMask(value)
            logger.debug("set single mask: $singleMask")
        }

    }


    private inner class ModePort : WriteOnlyPort() {

        override fun write(value: Int) {
            mode = Mode(value)
            logger.debug("set mode: $mode")
        }

    }

    private inner class ClearPort : WriteOnlyPort() {

        override fun write(value: Int) {
            logger.debug("clear")
            clearInternal()
        }

    }

    private fun clearInternal() {
        mode = Mode(0)
        singleMask = SingleMask(0)
    }

    private class Mode(value: Int) {
        val channel = value and 0b11
        val operation = Operation.values()[(value and 0b1100) shr 2]
        val isAutoInitializationEnabled = (value and 0b10000) > 0
        val direction = Direction.values()[(value and 0b100000) shr 5]
        val transferMode = TransferMode.values()[(value and 0b11000000) shr 6]

        override fun toString(): String = "channel=$channel" +
                ", operation=$operation" +
                ", isAutoInitializationEnabled=$isAutoInitializationEnabled" +
                ", direction=$direction" +
                ", transferMode=$transferMode"

        enum class Operation {
            VERIFY,
            WRITE,
            READ,
            RESERVED
        }

        enum class Direction {
            ADDRESS_INCREMENT,
            ADDRESS_DECREMENT
        }

        enum class TransferMode {
            DEMAND,
            SINGLE,
            BLOCK,
            CASCADE
        }
    }

    private class SingleMask(value: Int) {
        val channel = value and 0b11
        val action = Action.values()[(value and 0b100) shr 2]

        enum class Action {
            CLEAR,
            SET,
        }

        override fun toString(): String {
            return "channel=$channel,action=$action"
        }
    }

    private enum class Timing {
        COMPRESSED,
        NORMAL
    }

    private enum class Priority {
        FIXED,
        ROTATING
    }

    private enum class WriteSelection {
        LATE,
        EXTENDED
    }

}