package com.dpforge.ibmpc.drive

class FloppyDrive(private val image: ByteArray) : Drive {

    override fun read(
        sectorsToRead: Int,
        cylinder: Int,
        head: Int,
        sector: Int,
        sectorSize: Int,
        trackLength: Int,
    ): ByteArray =
        image.part(
            offset = sectorSize * chsToLba(cylinder, head, sector, trackLength),
            length = sectorsToRead * sectorSize
        )

    // https://en.wikipedia.org/wiki/Cylinder-head-sector
    // https://en.wikipedia.org/wiki/Logical_block_addressing
    private fun chsToLba(cylinder: Int, head: Int, sector: Int, trackLength: Int): Int =
        (cylinder * HEAD_AMOUNT + head) * trackLength + (sector - 1)

    private fun ByteArray.part(offset: Int, length: Int) = copyOfRange(offset, offset + length)

    private companion object {
        const val HEAD_AMOUNT = 2
    }

}
