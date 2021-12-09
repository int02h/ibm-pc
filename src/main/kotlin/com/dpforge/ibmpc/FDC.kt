package com.dpforge.ibmpc

import com.dpforge.ibmpc.drive.FloppyDrive
import com.dpforge.ibmpc.extensions.bit
import com.dpforge.ibmpc.extensions.bitInt
import com.dpforge.ibmpc.extensions.highNibble
import com.dpforge.ibmpc.extensions.lowNibble
import com.dpforge.ibmpc.extensions.toHex
import com.dpforge.ibmpc.extensions.withBit
import com.dpforge.ibmpc.extensions.withBitInt
import com.dpforge.ibmpc.port.Port
import com.dpforge.ibmpc.port.PortDevice
import com.dpforge.ibmpc.port.WriteOnlyPort
import org.slf4j.LoggerFactory

/**
 * Floppy Disk Controller
 * https://stanislavs.org/helppc/765.html
 */
class FDC(
    private val pic: PIC,
    private val dma: DMA,
    driveA: FloppyDrive?,
    driveB: FloppyDrive?,
) : PortDevice {

    private val logger = LoggerFactory.getLogger("FDC")

    private val results = mutableListOf<Int>()
    private val inputs = mutableListOf<Int>()

    private val status0 = Status0()
    private val status1 = Status1()
    private val status2 = Status2()

    private val drives = arrayOf(Drive(driveA), Drive(driveB))

    private var enabled = false
    private var isBusy = false
    private var ioDirection = IODirection.TO_FDC
    private var isDataRegisterReady = false
    private var pendingCommand: Command? = null
    private var wasReset = false
    private var dmaEnabled = false

    override fun getPortMapping(): Map<Int, Port> = mapOf(
        0x3f2 to DigitalOutputRegister(),
        0x3f4 to StatusRegister(),
        0x3f5 to CommandRegister(),
    )

    private inner class DigitalOutputRegister : WriteOnlyPort() {

        override fun write(value: Int) {
            val disk = value and 0b11
            val fdcEnabled = value.bit(2)
            dmaEnabled = value.bit(3)
            val driveMotorEnabled = booleanArrayOf(value.bit(4), value.bit(5))
            // bit 6-7 - reserved on PS/2

            logger.debug(
                "Digital Output Register. " +
                        "disk = $disk" +
                        ", FDC enabled = $fdcEnabled" +
                        ", DMA enabled = $dmaEnabled" +
                        ", drive motor enabled = ${driveMotorEnabled.contentToString()}"
            )

            if (!enabled && fdcEnabled) {
                isDataRegisterReady = true
                wasReset = true
                drives.forEach { drive -> drive.reset() }
                pic.onHardwareInterrupt(0x6)
            }

            enabled = fdcEnabled
        }

    }

    private inner class StatusRegister : Port {

        override fun write(value: Int) {
            TODO()
        }

        override fun read(): Int {
            var status = 0
            status = status.withBit(0, false) // drive 0 busy
            status = status.withBit(1, false) // drive 1 busy
            // bit 2 - drive 2 busy (reserved on PS/2)
            // bit 3 - drive 3 busy (reserved on PS/2)
            status = status.withBit(4, isBusy)
            status = status.withBit(5, !dmaEnabled) // non-DMA mode
            status = status.withBitInt(6, ioDirection.ordinal)
            status = status.withBit(7, isDataRegisterReady)
            return status
        }

    }

    /**
     * https://www.isdaman.com/alsos/hardware/fdc/floppy.htm
     */
    private inner class CommandRegister : Port {

        override fun write(value: Int) {
            if (pendingCommand == null) {
                inputs.clear()
                results.clear()
                pendingCommand = when (value and 0xF) {
                    0x3 -> FixDriveDataCommand()
                    0x5 -> WriteSectorCommand()
                    0x6 -> ReadSectorCommand()
                    0x7 -> CalibrateCommand()
                    0x8 -> CheckInterruptStatusCommand()
                    0xF -> ParkHeadCommand()
                    else -> error("Unsupported command ${(value and 0xF).toHex()}")
                }
            }
            inputs += value
            pendingCommand?.let { command ->
                if (inputs.size == command.argumentCount) {
                    isBusy = true
                    isDataRegisterReady = false
                    command.execute(inputs)
                    pendingCommand = null
                }
            }
        }

        override fun read(): Int {
            val result = results.removeAt(0)
            if (results.isEmpty()) {
                ioDirection = IODirection.TO_FDC
                isBusy = false
            }
            return result
        }

    }

    private class Status0 {

        var value: Int = 0
            private set

        fun set(
            drive: Int = 0,
            head: Int = 0,
            notReady: Boolean = false,
            unitCheck: Boolean = false,
            seekEnd: Boolean = false,
            interruptCode: Int = 0,
        ) {
            value = 0
            value = value or (drive and 0b11)
            value = value.withBitInt(2, head)
            value = value.withBit(3, notReady)
            value = value.withBit(4, unitCheck)
            value = value.withBit(5, seekEnd)
            value = value or ((interruptCode and 0b11) shl 6)
        }
    }

    private class Status1 {

        var value: Int = 0
            private set
    }

    private class Status2 {

        var value: Int = 0
            private set
    }

    private abstract inner class Command(val argumentCount: Int) {

        fun setResult(result: List<Int>) {
            results += result
            if (result.isNotEmpty()) {
                ioDirection = IODirection.FROM_FDC
            }
            isDataRegisterReady = true
            pic.onHardwareInterrupt(6)
        }

        abstract fun execute(input: List<Int>)
    }

    private inner class FixDriveDataCommand : Command(3) {
        override fun execute(input: List<Int>) {
            val stepRate = input[1].lowNibble
            val headUnloadTime = input[1].highNibble
            val headLoadTime = (input[2] shr 1) and 0b111_1111
            val nonDMAMode = input[2].bit(0)
            logger.debug(
                "Fix Drive Data: step rate = $stepRate" +
                        ", head unload time = $headUnloadTime" +
                        ", head load time = $headLoadTime" +
                        ", non DMA mode = $nonDMAMode"
            )
            setResult(emptyList())
        }
    }

    private abstract inner class AbstractSectorCommand : Command(9) {

        protected abstract fun log(message: String)

        protected abstract fun createDataRequest(
            drive: Drive,
            isMultiTrack: Boolean,
            sectorSizeInBytes: Int,
            trackLength: Int,
            onDone: () -> Unit
        ): DMA.Request

        override fun execute(input: List<Int>) {
            val isMultiTrack = input[0].bit(7)

            val driveIndex = input[1] and 0b11
            val drive = drives[driveIndex]
            drive.cylinder = input[2]
            drive.head = input[3]
            drive.sector = input[4]

            val sectorSize = input[5]
            val trackLength = input[6]
            val gap3Length = input[7]
            val dataLength = input[8]

            if (gap3Length != 42) {
                error("GAP 3 value $gap3Length is not supported yet")
            }
            if (dataLength != 0xFF) {
                error("Custom data length is not supported yet")
            }

            val sectorSizeInBytes = 128 * (1 shl sectorSize)

            log(
                "Start execution. Drive $driveIndex" +
                        ", cylinder = ${drive.cylinder}" +
                        ", head = ${drive.head}" +
                        ", sector = ${drive.sector}" +
                        ", sector size = $sectorSizeInBytes bytes" +
                        ", track length = $trackLength" +
                        ", multi-track = $isMultiTrack"
            )

            if (drive.isInserted) {
                dma.setRequest(
                    channel = 2,
                    request = createDataRequest(
                        drive = drive,
                        isMultiTrack = isMultiTrack,
                        sectorSizeInBytes = sectorSizeInBytes,
                        trackLength = trackLength,
                        onDone = {
                            log("Done")
                            status0.set(drive = driveIndex, head = drive.head)
                            setResult(
                                listOf(
                                    status0.value,
                                    status1.value,
                                    status2.value,
                                    drive.cylinder,
                                    drive.head,
                                    drive.sector,
                                    sectorSize
                                )
                            )
                        }
                    )
                )
            } else {
                status0.set(drive = driveIndex, head = drive.head, interruptCode = 0b10)
                setResult(
                    listOf(
                        status0.value,
                        status1.value,
                        status2.value,
                        drive.cylinder,
                        drive.head,
                        drive.sector,
                        sectorSize
                    )
                )
            }
        }
    }

    private inner class WriteSectorCommand : AbstractSectorCommand() {

        override fun log(message: String) {
            logger.debug("[Write sector] $message")
        }

        override fun createDataRequest(
            drive: Drive,
            isMultiTrack: Boolean,
            sectorSizeInBytes: Int,
            trackLength: Int,
            onDone: () -> Unit
        ): DMA.Request = WriteDataRequest(
            drive = drive,
            isMultiTrack = isMultiTrack,
            sectorSizeInBytes = sectorSizeInBytes,
            trackLength = trackLength,
            onDone = onDone,
        )

    }

    private inner class ReadSectorCommand : AbstractSectorCommand() {

        override fun log(message: String) {
            logger.debug("[Read sector] $message")
        }

        override fun createDataRequest(
            drive: Drive,
            isMultiTrack: Boolean,
            sectorSizeInBytes: Int,
            trackLength: Int,
            onDone: () -> Unit
        ): DMA.Request = ReadDataRequest(
            drive = drive,
            isMultiTrack = isMultiTrack,
            sectorSizeInBytes = sectorSizeInBytes,
            trackLength = trackLength,
            onDone = onDone,
        )

    }

    private inner class CalibrateCommand : Command(2) {

        override fun execute(input: List<Int>) {
            val driveIndex = input[1] and 0b11
            val drive = drives[driveIndex]
            drive.cylinder = 0
            status0.set(drive = driveIndex, seekEnd = true)
            logger.debug("Calibrate drive $drive")
            setResult(emptyList())
        }

    }

    private inner class CheckInterruptStatusCommand : Command(1) {
        override fun execute(input: List<Int>) {
            logger.debug("Check interrupt status")
            setResult(
                if (wasReset) {
                    wasReset = false
                    listOf(0xC0)
                } else {
                    listOf(0x80)
                }
            )
        }
    }

    private inner class ParkHeadCommand : Command(3) {

        override fun execute(input: List<Int>) {
            val driveIndex = input[1] and 0b11
            val drive = drives[driveIndex]
            drive.head = input[1].bitInt(3)
            drive.cylinder = input[2]
            logger.debug("Park head ${drive.head} of drive $driveIndex to cylinder ${drive.cylinder}")
            setResult(emptyList())
        }

    }

    private enum class IODirection {
        TO_FDC,
        FROM_FDC
    }

    private class Drive(
        val floppyDrive: FloppyDrive?,
    ) {
        var cylinder: Int = 0
        var head: Int = 0
        var sector: Int = 1

        val isInserted: Boolean = floppyDrive != null

        fun ensureInserted() = floppyDrive ?: error("Drive not inserted")

        fun reset() {
            cylinder = 0
            head = 0
            sector = 1
        }
    }

    private abstract class AbstractDataRequest(
        val drive: Drive,
        val isMultiTrack: Boolean,
        val trackLength: Int,
    ) : DMA.Request {

        protected fun moveToNextSector() {
            drive.sector++
            if (drive.sector > trackLength || drive.sector > drive.ensureInserted().sectorsPerTrack) {
                drive.sector = 1
                if (isMultiTrack) {
                    drive.head++
                    if (drive.head > 1) {
                        drive.head = 0
                        drive.cylinder++
                    }
                } else {
                    drive.cylinder++
                }
            }
        }

    }

    private class WriteDataRequest(
        drive: Drive,
        isMultiTrack: Boolean,
        val sectorSizeInBytes: Int,
        trackLength: Int,
        val onDone: () -> Unit
    ) : AbstractDataRequest(drive, isMultiTrack, trackLength) {

        private val data = ByteArray(sectorSizeInBytes)
        private var index = 0

        override fun readNextByte(): Int {
            error("This is write request")
        }

        override fun writeNextByte(byte: Int) {
            data[index++] = (byte and 0xFF).toByte()
            if (index == data.size) {
                writeData()
                moveToNextSector()
                index = 0
            }
        }

        override fun onTransferDone() {
            if (index > 0) {
                TODO("Data is partially filled")
            }
            onDone()
        }

        private fun writeData(): Unit = drive.ensureInserted().write(
            data = data,
            cylinder = drive.cylinder,
            head = drive.head,
            sector = drive.sector,
            sectorSize = sectorSizeInBytes,
        )

    }

    private inner class ReadDataRequest(
        drive: Drive,
        isMultiTrack: Boolean,
        val sectorSizeInBytes: Int,
        trackLength: Int,
        val onDone: () -> Unit
    ) : AbstractDataRequest(drive, isMultiTrack, trackLength) {

        private val data = ByteArray(sectorSizeInBytes)
        private var index = -1

        override fun readNextByte(): Int {
            if (index == -1) {
                readData()
                index = 0
            } else if (index >= data.size) {
                moveToNextSector()
                readData()
                index = 0
            }
            return data[index++].toInt() and 0xFF
        }

        override fun writeNextByte(byte: Int) {
            error("This is read request")
        }

        override fun onTransferDone() {
            onDone()
        }

        private fun readData(): Int = drive.ensureInserted().read(
            buffer = data,
            cylinder = drive.cylinder,
            head = drive.head,
            sector = drive.sector,
            sectorSize = sectorSizeInBytes,
        )

    }
}