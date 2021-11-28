package com.dpforge.ibmpc

import com.dpforge.ibmpc.extensions.bit
import com.dpforge.ibmpc.extensions.bitInt
import com.dpforge.ibmpc.extensions.lsb
import com.dpforge.ibmpc.extensions.msb
import com.dpforge.ibmpc.extensions.toHex
import com.dpforge.ibmpc.memory.Memory
import com.dpforge.ibmpc.port.Port
import com.dpforge.ibmpc.port.PortDevice
import com.dpforge.ibmpc.port.WriteOnlyPort
import org.slf4j.LoggerFactory

/**
 * Direct Memory Access, Intel 8237
 */
class DMA(
    private val memory: Memory
) : PortDevice {

    private val logger = LoggerFactory.getLogger("DMA")

    private val channels = Array(4) { Channel(it) }

    private var flipFlop = false

    override fun getPortMapping(): Map<Int, Port> = mapOf(
        0x00 to AddressRegister(channelIndex = 0),
        0x01 to WordCount(channelIndex = 0),
        0x02 to AddressRegister(channelIndex = 1),
        0x03 to WordCount(channelIndex = 1),
        0x04 to AddressRegister(channelIndex = 2),
        0x05 to WordCount(channelIndex = 2),
        0x06 to AddressRegister(channelIndex = 3),
        0x07 to WordCount(channelIndex = 3),
        0x08 to StatusCommandRegister(),
        0x0A to MaskRegister(),
        0x0B to ModeRegister(),
        0x0C to ClearFlipFlop(),
        0x0D to ResetPort(),
        0x81 to PageRegister(2),
        0x82 to PageRegister(3),
        0x83 to PageRegister(1),
    )

    // DREQ
    fun setRequest(channel: Int, request: Request) {
        channels[channel].request = request
    }

    fun onCPUCycle() {
        channels.forEach { channel ->
            if (channel.request != null && channel.enabled) {
                transferData(channel)
            }
        }
    }

    private fun transferData(channel: Channel): Unit =
        when (channel.mode.transferMode) {
            Mode.TransferMode.DEMAND -> TODO()
            Mode.TransferMode.SINGLE -> transferSingle(channel)
            Mode.TransferMode.BLOCK -> TODO()
            Mode.TransferMode.CASCADE -> TODO()
        }

    private fun transferSingle(channel: Channel): Unit =
        when (channel.mode.operation) {
            Mode.Operation.VERIFY -> TODO()
            Mode.Operation.WRITE -> singleWrite(channel)
            Mode.Operation.READ -> TODO()
            Mode.Operation.RESERVED -> TODO()
        }

    private fun singleWrite(channel: Channel) {
        val request = channel.request ?: error("No request")
        if (channel.wordCount > 0) {
            val b = request.getNextByte()
            memory.setByte(channel.physicalAddress, b)
            channel.address += channel.mode.direction.addressDiff
            channel.wordCount -= 1
        }
        if (channel.wordCount == 0) {
            request.onTransferDone()
            channel.request = null
        }
    }

    private inner class AddressRegister(val channelIndex: Int) : Port {

        private val channel: Channel
            get() = channels[channelIndex]

        override fun write(value: Int) {
            if (!flipFlop) {
                flipFlop = true
                channel.address = (channel.address and 0xFF00) or value
            } else {
                flipFlop = false
                channel.address = channel.address.lsb or (value shl 8)
            }
        }

        override fun read(): Int =
            if (!flipFlop) {
                flipFlop = true
                channel.address.lsb
            } else {
                flipFlop = false
                channel.address.msb
            }

    }

    private inner class WordCount(val channelIndex: Int) : Port {

        private val channel: Channel
            get() = channels[channelIndex]

        override fun write(value: Int) {
            if (!flipFlop) {
                flipFlop = true
                channel.wordCount = (channel.wordCount and 0xFF00) or value
            } else {
                flipFlop = false
                channel.wordCount = channel.wordCount.lsb or (value shl 8)
            }
        }

        override fun read(): Int =
            if (!flipFlop) {
                flipFlop = true
                channel.wordCount.lsb
            } else {
                flipFlop = false
                channel.wordCount.msb
            }

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

        // status
        override fun read(): Int {
            TODO("Not yet implemented")
        }

    }

    private inner class MaskRegister : Port {

        override fun write(value: Int) {
            val channel = value and 0b11
            val disable = value.bit(2)
            channels[channel].enabled = !disable
            if (disable) {
                logger.debug("Disable $channel")
            } else {
                logger.debug("Enable $channel")
            }
        }

        override fun read(): Int {
            TODO("Not yet implemented")
        }

    }

    private inner class ModeRegister : WriteOnlyPort() {

        override fun write(value: Int) {
            val channel = value and 0b11
            val mode = Mode(value)
            channels[channel].mode = mode
            logger.debug("Set mode $mode for $channel")
        }

    }

    private inner class ClearFlipFlop : WriteOnlyPort() {

        override fun write(value: Int) {
            flipFlop = false
        }

    }

    private inner class ResetPort : WriteOnlyPort() {

        override fun write(value: Int) {
            logger.debug("reset")
            flipFlop = false
            for (i in channels.indices) {
                channels[i] = Channel(i)
            }
        }

    }

    private inner class PageRegister(val channelIndex: Int) : Port {

        private val channel: Channel
            get() = channels[channelIndex]

        override fun write(value: Int) {
            channel.page = value and 0b1111
            logger.debug("Set page $value (${value.toHex()}) for channel $channel")
        }

        override fun read(): Int {
            TODO("Not yet implemented")
        }

    }

    private class Channel(val index: Int) {
        private var _mode: Mode? = null
        var mode: Mode
            get() = _mode ?: error("Channel $index is not initialized")
            set(value) {
                _mode = value
            }

        var request: Request? = null

        var address: Int = 0
        var wordCount: Int = 0
        var page: Int = 0
        var enabled = false

        val physicalAddress: Int
            get() = address + (page shl 16)
    }

    private class Mode(value: Int) {
        val operation = Operation.values()[(value and 0b1100) shr 2]
        val isAutoInitializationEnabled = (value and 0b10000) > 0
        val direction = Direction.values()[(value and 0b100000) shr 5]
        val transferMode = TransferMode.values()[(value and 0b11000000) shr 6]

        override fun toString(): String = "operation=$operation" +
                ", isAutoInitializationEnabled=$isAutoInitializationEnabled" +
                ", direction=$direction" +
                ", transferMode=$transferMode"

        enum class Operation {
            VERIFY,
            WRITE,
            READ,
            RESERVED
        }

        enum class Direction(val addressDiff: Int) {
            ADDRESS_INCREMENT(1),
            ADDRESS_DECREMENT(-1)
        }

        enum class TransferMode {
            DEMAND,
            SINGLE,
            BLOCK,
            CASCADE
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

    interface Request {
        fun getNextByte(): Int
        fun onTransferDone()
    }

}