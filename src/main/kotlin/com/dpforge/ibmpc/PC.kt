package com.dpforge.ibmpc

import com.dpforge.ibmpc.cpu.CPU
import com.dpforge.ibmpc.drive.FloppyDrive
import com.dpforge.ibmpc.extensions.toHex
import com.dpforge.ibmpc.graphic.CGA
import com.dpforge.ibmpc.graphic.Display
import com.dpforge.ibmpc.graphic.MDA
import com.dpforge.ibmpc.graphic.RetraceSynchronizer
import com.dpforge.ibmpc.memory.BiosROM
import com.dpforge.ibmpc.memory.ConventionalMemory
import com.dpforge.ibmpc.memory.Memory
import com.dpforge.ibmpc.memory.VideoRAM
import com.dpforge.ibmpc.port.IgnoredPortDevice
import com.dpforge.ibmpc.port.Ports
import java.io.File
import org.slf4j.LoggerFactory

class PC(
    config: Config,
) {

    private val logger = LoggerFactory.getLogger("PC")

    val cpu: CPU

    init {
        val bios = config.biosROM.readBytes()

        val videoRAM = VideoRAM()
        val memory = Memory(
            conventionalMemory = ConventionalMemory(),
            videoRAM = videoRAM,
            bios = BiosROM(bios, buildBASIC(config.cassetteBASICImages))
        )

        val pic = PIC()
        val pit = PIT(pic)
        val retraceSynchronizer = RetraceSynchronizer()
        val cga = CGA(memory, retraceSynchronizer)
        val dma = DMA(memory)

        val ppi = PPI(
            pic = pic,
            equipment = PPI.Equipment(
                hasBootFloppyDrive = config.driveA != null,
                floppyDriveAmount = listOf(config.driveA, config.driveB).count { it != null }
            )
        )

        val ports = Ports().apply {
            connect(dma)
            connect(CMOS())
            connect(pic)
            connect(MDA())
            connect(cga)
            connect(ppi)
            connect(pit)
            connect(ExpansionCard())
            connect(LPT())
            connect(ProgrammableNoiseGenerator())
            connect(UART())
            connect(GameAdapter())
            connect(ExternalDevices())
            connect(IgnoredPortDevice())
            connect(
                FDC(
                    pic = pic,
                    dma = dma,
                    driveA = config.driveA?.let { FloppyDrive(it.readBytes()) },
                    driveB = config.driveB?.let { FloppyDrive(it.readBytes()) },
                )
            )
        }

        Display(videoRAM, cga, Keyboard(ppi))

        cpu = CPU(
            memory = memory,
            ports = ports,
            pic = pic,
            cycleListener = {
                pit.update()
                dma.onCPUCycle()
                retraceSynchronizer.onCPUCycle()
            }
        )
    }

    fun start() {
        cpu.reset()
        try {
            while (true) {
                cpu.executeNext()
            }
        } catch (error: Throwable) {
            logger.error("code offset = ${cpu.codeOffset.toHex()}", error)
        }
    }

    private fun buildBASIC(images: List<File>?): ByteArray? {
        if (images == null) {
            return null
        }
        val data = images.map { it.readBytes() }
        val size = data.sumOf { it.size }
        val basic = ByteArray(size)
        var destinationOffset = 0
        data.forEach {
            it.copyInto(destination = basic, destinationOffset = destinationOffset)
            destinationOffset += it.size
        }
        return basic
    }

}