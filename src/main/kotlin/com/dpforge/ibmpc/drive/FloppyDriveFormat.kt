package com.dpforge.ibmpc.drive

/**
 * https://www.classicdosgames.com/tutorials/disks.html
 */
enum class FloppyDriveFormat(
    val headAmount: Int,
    val sectorsPerTrack: Int,
    val sectorSize: Int,
    val capacityBytes: Int
) {
    FLOPPY_5_25_160KB(
        headAmount = 1,
        sectorsPerTrack = 8,
        sectorSize = 512,
        capacityBytes = 160.kb
    ),

    FLOPPY_5_25_320KB(
        headAmount = 2,
        sectorsPerTrack = 8,
        sectorSize = 512,
        capacityBytes = 320.kb
    ),

    FLOPPY_5_25_360KB(
        headAmount = 2,
        sectorsPerTrack = 9,
        sectorSize = 512,
        capacityBytes = 360.kb
    ),

    FLOPPY_5_25_1200KB(
        headAmount = 2,
        sectorsPerTrack = 15,
        sectorSize = 512,
        capacityBytes = 1200.kb
    ),

    FLOPPY_3_5_720KB(
        headAmount = 2,
        sectorsPerTrack = 9,
        sectorSize = 512,
        capacityBytes = 720.kb
    ),

    FLOPPY_3_5_1440KB(
        headAmount = 2,
        sectorsPerTrack = 18,
        sectorSize = 512,
        capacityBytes = 1440.kb
    )

}

val Int.kb: Int
    get() = this * 1024

