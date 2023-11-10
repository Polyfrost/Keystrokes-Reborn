package org.polyfrost.polykeystrokes.gui

import cc.polyfrost.oneconfig.libs.universal.UResolution
import cc.polyfrost.oneconfig.utils.dsl.VG
import cc.polyfrost.oneconfig.utils.dsl.drawLine

private const val SNAP_RANGE = 1
private const val LINE_COLOR = 0xFF8A2BE2.toInt()

@JvmInline
value class SnappingLines(private val lines: Set<Int>) {
    constructor(lines: Iterable<Int>) : this(lines.toSet())

    fun findSnap(value: Int) = lines.find { snap ->
        val distance = snap - value
        distance in -SNAP_RANGE..SNAP_RANGE
    }

    fun findSnapAndSet(value: Int, setter: (Int) -> Unit) = findSnap(value)?.also(setter)
}

fun VG.drawLines(lineX: Int?, lineY: Int?) {
    drawLineX(lineX)
    drawLineY(lineY)
}

private fun VG.drawLineX(lineX: Int?) {
    val x = lineX ?: return
    drawLine(
        x1 = x,
        y1 = 0,
        x2 = x,
        y2 = UResolution.scaledHeight,
        width = 1,
        color = LINE_COLOR
    )
}

private fun VG.drawLineY(lineY: Int?) {
    val y = lineY ?: return
    drawLine(
        x1 = 0,
        y1 = y,
        x2 = UResolution.scaledWidth,
        y2 = y,
        width = 1,
        color = LINE_COLOR
    )
}


