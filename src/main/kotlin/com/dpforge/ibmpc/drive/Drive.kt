package com.dpforge.ibmpc.drive

interface Drive {

    val parameters: Parameters

    fun read(sectorsToRead: Int, cylinder: Int, head: Int, sector: Int): ByteArray

    // https://stanislavs.org/helppc/int_13-8.html
    class Parameters(
        val driveType: Int,
        val cylinders: Int,
        val sectorsPerTrack: Int,
        val sides: Int,
        val drivesAttached: Int,
        val dbtSector: Int,
        val dbtOffset: Int,
    )

}