package com.dpforge.ibmpc.drive

class FloppyDrive(
    private val image: ByteArray,
    private val format: FloppyDriveFormat,
) {

    val sectorsPerTrack: Int = format.sectorsPerTrack

    fun read(
        buffer: ByteArray,
        cylinder: Int,
        head: Int,
        sector: Int,
        sectorSize: Int,
    ): Int {
        if (format.sectorSize != sectorSize) {
            error("Expected sector size is ${format.sectorSize} but was $sectorSize")
        }
        image.part(
            destination = buffer,
            offset = sectorSize * chsToLba(cylinder, head, sector),
            length = sectorSize
        )
        return sectorSize * chsToLba(cylinder, head, sector)
    }

    fun write(
        data: ByteArray,
        cylinder: Int,
        head: Int,
        sector: Int,
        sectorSize: Int,
    ) {
        if (format.sectorSize != sectorSize) {
            error("Expected sector size is ${format.sectorSize} but was $sectorSize")
        }
        val offset = sectorSize * chsToLba(cylinder, head, sector)
        data.copyInto(image, offset)
    }

    // https://en.wikipedia.org/wiki/Cylinder-head-sector
    // https://en.wikipedia.org/wiki/Logical_block_addressing
    private fun chsToLba(cylinder: Int, head: Int, sector: Int): Int =
        (cylinder * format.headAmount + head) * format.sectorsPerTrack + (sector - 1)

    private fun ByteArray.part(destination: ByteArray, offset: Int, length: Int) =
        copyInto(
            destination = destination,
            destinationOffset = 0,
            startIndex = offset,
            endIndex = offset + length
        )

}
