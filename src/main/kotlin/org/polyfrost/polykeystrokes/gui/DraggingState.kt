package org.polyfrost.polykeystrokes.gui

import cc.polyfrost.oneconfig.utils.dsl.VG
import cc.polyfrost.oneconfig.utils.dsl.drawRect
import cc.polyfrost.oneconfig.utils.dsl.nanoVG
import org.polyfrost.polykeystrokes.config.Element
import org.polyfrost.polykeystrokes.config.ModConfig
import org.polyfrost.polykeystrokes.util.IntRectangle

private const val SELECTION_COLOR = 0x640000FF

interface DraggingState {
    fun draw(mouseX: Int, mouseY: Int) = nanoVG(mcScaling = true) {
        draw(mouseX, mouseY)
    }

    fun VG.draw(mouseX: Int, mouseY: Int)

    class Dragging(
        mouseX: Int, mouseY: Int, selectedKeys: ElementList, dragging: Element,
    ) : DraggingState {
        private val draggedPosition = dragging.position
        private val draggedOffsetted = draggedPosition.offsetTo(mouseX, mouseY)
        private val offsettedPositions = (selectedKeys - dragging).map { element -> element.position.offsetTo(draggedPosition) }
        private val filteredKeys = ModConfig.elements - selectedKeys
        private val xCenters = SnappingLines(filteredKeys.map { it.position.xCenter })
        private val xSides = SnappingLines(filteredKeys.flatMap { listOf(it.position.x, it.position.xRight) })
        private val yCenters = SnappingLines(filteredKeys.map { it.position.yCenter })
        private val ySides = SnappingLines(filteredKeys.flatMap { listOf(it.position.y, it.position.yBottom) })
        private var lineX: Int? = null
        private var lineY: Int? = null

        fun updateMoveSnapX(mouseX: Int) {
            draggedOffsetted.x = mouseX

            lineX = with(draggedPosition) {
                xCenters.findSnap(xCenter)?.also { xCenter = it }
                    ?: xSides.findSnap(x)?.also { x = it }
                    ?: xSides.findSnap(xRight)?.also { xRight = it }
            }

            for (offsetedPosition in offsettedPositions) {
                offsetedPosition.x = draggedPosition.x
            }
        }

        fun updateMoveSnapY(mouseY: Int) {
            draggedOffsetted.y = mouseY

            lineY = with(draggedPosition) {
                yCenters.findSnap(yCenter)?.also { yCenter = it }
                    ?: ySides.findSnap(y)?.also { y = it }
                    ?: ySides.findSnap(yBottom)?.also { yBottom = it }
            }

            for (offsetedPosition in offsettedPositions) {
                offsetedPosition.y = draggedPosition.y
            }
        }

        override fun VG.draw(mouseX: Int, mouseY: Int) = drawLines(lineX, lineY)
    }

    class Resizing(
        selectedKeys: ElementList, resizing: Element,
    ) : DraggingState {
        private val draggedPosition = resizing.position
        private val offsettedPositions = (selectedKeys - resizing).map { element -> element.position.offsetTo(draggedPosition) }
        private val filteredKeys = ModConfig.elements - selectedKeys
        private val xSides = SnappingLines(filteredKeys.flatMap { listOf(it.position.x, it.position.xRight) })
        private val ySides = SnappingLines(filteredKeys.flatMap { listOf(it.position.y, it.position.yBottom) })
        private var lineX: Int? = null
        private var lineY: Int? = null

        fun updateResizeSnapX(mouseX: Int) {
            draggedPosition.width = mouseX - draggedPosition.x

            lineX = with(draggedPosition) {
                xSides.findSnap(xRight)?.also { width = it - x }
            }

            for (offsetedPosition in offsettedPositions) {
                offsetedPosition.width = draggedPosition.width
            }
        }

        fun updateResizeSnapY(mouseY: Int) {
            draggedPosition.height = mouseY - draggedPosition.y

            lineY = with(draggedPosition) {
                ySides.findSnap(yBottom)?.also { height = it - y }
            }

            for (offsetedPosition in offsettedPositions) {
                offsetedPosition.height = draggedPosition.height
            }
        }

        override fun VG.draw(mouseX: Int, mouseY: Int) = drawLines(lineX, lineY)
    }

    class Selecting(
        private val clickedMouseX: Int,
        private val clickedMouseY: Int,
    ) : DraggingState {
        fun getSelectionBox(currentMouseX: Int, currentMouseY: Int): IntRectangle {
            val (xLeft, xRight) = sortLessToGreater(clickedMouseX, currentMouseX)
            val (yTop, yBottom) = sortLessToGreater(clickedMouseY, currentMouseY)
            val width = xRight - xLeft
            val height = yBottom - yTop
            return IntRectangle(xLeft, yTop, width, height)
        }

        override fun VG.draw(mouseX: Int, mouseY: Int) = with(getSelectionBox(mouseX, mouseY)) {
            drawRect(x, y, width, height, SELECTION_COLOR)
        }
    }
}

private fun sortLessToGreater(first: Int, second: Int) =
    if (first > second) second to first
    else first to second
