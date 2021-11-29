package com.dpforge.ibmpc.drive

interface Drive {

    fun read(
        sectorsToRead: Int,
        cylinder: Int,
        head: Int,
        sector: Int,
        sectorSize: Int,
        trackLength: Int,
    ): ByteArray

}