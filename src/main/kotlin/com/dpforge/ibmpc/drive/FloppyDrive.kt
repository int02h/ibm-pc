package com.dpforge.ibmpc.drive

class FloppyDrive(private val image: ByteArray) {

    val sectorsPerTrack: Int = SECTORS_PER_TRACK

    fun read(
        buffer: ByteArray,
        cylinder: Int,
        head: Int,
        sector: Int,
        sectorSize: Int,
    ): Int {
        image.part(
            destination = buffer,
            offset = sectorSize * chsToLba(cylinder, head, sector),
            length = sectorSize
        )
        return sectorSize * chsToLba(cylinder, head, sector)
    }

    // https://en.wikipedia.org/wiki/Cylinder-head-sector
    // https://en.wikipedia.org/wiki/Logical_block_addressing
    private fun chsToLba(cylinder: Int, head: Int, sector: Int): Int =
        (cylinder * HEAD_AMOUNT + head) * SECTORS_PER_TRACK + (sector - 1)

    private fun ByteArray.part(destination: ByteArray, offset: Int, length: Int) =
        copyInto(
            destination = destination,
            destinationOffset = 0,
            startIndex = offset,
            endIndex = offset + length
        )

    private companion object {
        const val HEAD_AMOUNT = 2
        const val SECTORS_PER_TRACK = 9
    }

}
