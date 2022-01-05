package com.dpforge.ibmpc.graphic

import com.dpforge.ibmpc.extensions.exhaustive
import kotlin.math.roundToLong

class RetraceSynchronizer {

    var inHorizontalRetrace = false
        private set
    var inVerticalRetrace = false
        private set

    private var verticalRetraceDurationNanos: Long = 0L
    private var horizontalRetraceDurationNanos: Long = 0L
    private var nanosPerCell: Long = 0L
    private var cellsPerRow: Long = 0
    private var rowCount: Long = 0
    private var modeSet = false

    private var cpuCycles = 0L
    private var horizontalRetraceStartTimeNanos = 0L
    private var verticalRetraceStartTimeNanos = 0L
    private var rowStartTimeNanos = 0L
    private var rowIndex = 0L

    private var prevMode: CGA.Mode? = null

    // https://www.vcfed.org/forum/forum/technical-support/vintage-computer-programming/71067-cga-timing-issue?p=861219#post861219
    fun setMode(mode: CGA.Mode) {
        if (mode == prevMode) {
            return
        }
        when (mode) {
            CGA.Mode.TEXT_80x25 -> {
                verticalRetraceDurationNanos = millisToNanos(1.25)
                horizontalRetraceDurationNanos = millisToNanos(1.25)
                nanosPerCell = 559
                cellsPerRow = 80
                rowCount = 25
            }
            CGA.Mode.TEXT_40x25 -> {
                verticalRetraceDurationNanos = millisToNanos(1.25)
                horizontalRetraceDurationNanos = millisToNanos(1.25)
                nanosPerCell = microsToNanos(1.12)
                cellsPerRow = 40
                rowCount = 25
            }
            CGA.Mode.GRAPHICS_COLOR_320x200 -> { // TODO fix
                verticalRetraceDurationNanos = millisToNanos(1.25)
                horizontalRetraceDurationNanos = millisToNanos(1.25)
                nanosPerCell = 559
                cellsPerRow = 80
                rowCount = 25
            }
            CGA.Mode.GRAPHICS_BW_320x200 -> TODO()
            CGA.Mode.GRAPHICS_BW_640x200 -> TODO()
        }.exhaustive

        modeSet = true
        cpuCycles = 0L
        prevMode = mode
    }

    fun onCPUCycle() {
        if (!modeSet) {
            error("Mode is not set")
        }
        cpuCycles++
        val nanos = cpuCycles * 838L

        if (inVerticalRetrace) {
            val elapsedNanos = nanos - verticalRetraceStartTimeNanos
            if (elapsedNanos > verticalRetraceDurationNanos) {
                inVerticalRetrace = false
                cpuCycles = 0
                rowStartTimeNanos = 0
                rowIndex = 0
            }
        } else {
            if (inHorizontalRetrace) {
                val elapsedNanos = nanos - horizontalRetraceStartTimeNanos
                if (elapsedNanos > horizontalRetraceDurationNanos) {
                    inHorizontalRetrace = false
                    rowStartTimeNanos = nanos
                    rowIndex++
                }
            } else {
                val elapsed = nanos - rowStartTimeNanos
                val cellDrawnAmount = elapsed / nanosPerCell
                if (cellDrawnAmount > cellsPerRow) {
                    inHorizontalRetrace = true
                    horizontalRetraceStartTimeNanos = nanos
                }
            }
            if (rowIndex >= rowCount) {
                inVerticalRetrace = true
                verticalRetraceStartTimeNanos = nanos
            }
        }
    }

    companion object {
        private fun millisToNanos(millis: Double): Long = (millis * 1_000_000).roundToLong()
        private fun microsToNanos(micros: Double): Long = (micros * 1_000).roundToLong()
    }
}