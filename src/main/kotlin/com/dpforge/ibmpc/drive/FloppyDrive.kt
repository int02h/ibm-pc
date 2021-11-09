package com.dpforge.ibmpc.drive

class FloppyDrive(val image: ByteArray) : Drive {

    override val parameters: Drive.Parameters = Drive.Parameters(
        driveType = 4,
        cylinders = 80,
        sectorsPerTrack = SECTOR_PER_TRACK,
        sides = 2,
        drivesAttached = 1,

        // it made intentionally until proper BIOS implementation.
        // It will crash emulator when reading 11 at the last memory address
        dbtSector = 0xFFFF,
        dbtOffset = 0xFFFF
    )

    override fun read(sectorsToRead: Int, cylinder: Int, head: Int, sector: Int): ByteArray =
        image.part(
            offset = SECTOR_SIZE * chsToLba(cylinder, head, sector),
            length = sectorsToRead * SECTOR_SIZE
        )

    // https://en.wikipedia.org/wiki/Cylinder-head-sector
    // https://en.wikipedia.org/wiki/Logical_block_addressing
    private fun chsToLba(cylinder: Int, head: Int, sector: Int): Int =
        (cylinder * HEAD_AMOUNT + head) * SECTOR_PER_TRACK + (sector - 1)

    private fun ByteArray.part(offset: Int, length: Int) = copyOfRange(offset, offset + length)

    private companion object {
        const val SECTOR_SIZE = 512
        const val HEAD_AMOUNT = 2
        const val SECTOR_PER_TRACK = 18
    }

}
