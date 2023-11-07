package org.polyfrost.polykeystrokes.gui

import cc.polyfrost.oneconfig.libs.universal.UResolution
import cc.polyfrost.oneconfig.utils.dsl.VG
import cc.polyfrost.oneconfig.utils.dsl.drawLine
import org.polyfrost.polykeystrokes.config.Element

private const val SNAP_RANGE = 1
private const val LINE_COLOR = 0xFF8A2BE2.toInt()

private fun findSnap(value: Int, snaps: List<Int>) = snaps.find { snap ->
    val offset = (snap - value)
    offset in -SNAP_RANGE..SNAP_RANGE
}
// todo: someone rewrite this
open class Snapping(
    private var currentMouseX: Int,
    private var currentMouseY: Int,
    excludeKeys: ElementList,
    private val dragged: Element,
) : DraggingState.DrawableState {
    private val filteredKeys = elements - excludeKeys
    private val xCenters = filteredKeys.map { key -> key.position.xCenter }
    private val xSides = filteredKeys.flatMap { key -> listOf(key.position.x, key.position.xRight) }
    private val yCenters = filteredKeys.map { key -> key.position.yCenter }
    private val ySides = filteredKeys.flatMap { key -> listOf(key.position.y, key.position.yBottom) }
    private var lineX: Int? = null
    private var lineY: Int? = null

    fun getSnappedXChange(mouseX: Int): Int {
        val snappedX = updateSnapWithX(mouseX, dragged.position.xCenter, xCenters)
            ?: updateSnapWithX(mouseX, dragged.position.x, xSides)
            ?: updateSnapWithX(mouseX, dragged.position.xRight, xSides)
            ?: mouseX
        val xChange = snappedX - currentMouseX
        currentMouseX = snappedX
        return xChange
    }

    fun getSnappedXRightChange(mouseX: Int): Int {
        val snappedX = updateSnapWithX(mouseX, dragged.position.xRight, xSides)
            ?: mouseX
        val xChange = snappedX - currentMouseX
        currentMouseX = snappedX
        return xChange
    }

    fun getSnappedYChange(mouseY: Int): Int {
        val snappedY = updateSnapWithY(mouseY, dragged.position.yCenter, yCenters)
            ?: updateSnapWithY(mouseY, dragged.position.y, ySides)
            ?: updateSnapWithY(mouseY, dragged.position.yBottom, ySides)
            ?: mouseY
        val yChange = snappedY - currentMouseY
        currentMouseY = snappedY
        return yChange
    }

    fun getSnappedYBottomChange(mouseY: Int): Int {
        val snappedY = updateSnapWithY(mouseY, dragged.position.yBottom, ySides)
            ?: mouseY
        val yChange = snappedY - currentMouseY
        currentMouseY = snappedY
        return yChange
    }

    private fun updateSnapWithX(x: Int, with: Int, snaps: List<Int>) =
        findSnap(with, snaps).also { lineXOrNull ->
            lineX = lineXOrNull
        }?.plus(x - with)

    private fun updateSnapWithY(y: Int, with: Int, snaps: List<Int>) =
        findSnap(with, snaps).also { lineYOrNull ->
            lineY = lineYOrNull
        }?.plus(y - with)

    override fun VG.draw(mouseX: Int, mouseY: Int) {
        drawLineX()
        drawLineY()
    }

    private fun VG.drawLineX() {
        val lineX = lineX ?: return
        drawLine(
            x1 = lineX,
            y1 = 0,
            x2 = lineX,
            y2 = UResolution.scaledHeight,
            width = 1,
            color = LINE_COLOR
        )
    }

    private fun VG.drawLineY() {
        val lineY = lineY ?: return
        drawLine(
            x1 = 0,
            y1 = lineY,
            x2 = UResolution.scaledWidth,
            y2 = lineY,
            width = 1,
            color = LINE_COLOR
        )
    }
}


