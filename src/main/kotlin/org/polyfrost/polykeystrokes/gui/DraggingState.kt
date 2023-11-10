package org.polyfrost.polykeystrokes.gui

import cc.polyfrost.oneconfig.utils.dsl.VG
import cc.polyfrost.oneconfig.utils.dsl.drawRect
import cc.polyfrost.oneconfig.utils.dsl.nanoVG
import org.polyfrost.polykeystrokes.config.Element
import org.polyfrost.polykeystrokes.config.ModConfig
import org.polyfrost.polykeystrokes.util.IntRectangle
import org.polyfrost.polykeystrokes.util.Rectangle

private const val SELECTION_COLOR = 0x640000FF

interface DraggingState {
    fun draw(mouseX: Int, mouseY: Int) = nanoVG(mcScaling = true) {
        draw(mouseX, mouseY)
    }

    fun VG.draw(mouseX: Int, mouseY: Int)

    class Dragging(
        mouseX: Int, mouseY: Int, selected: ElementUnion,
    ) : DraggingState {
        private val selectedPos: Rectangle = selected.position
        private val xOffset: Int = mouseX - selectedPos.x
        private val yOffset: Int = mouseY - selectedPos.y
        private val filteredKeys: List<Element> = ModConfig.elements - selected.elements
        private val xCenters = SnappingLines(filteredKeys.map { it.position.xCenter })
        private val xSides = SnappingLines(filteredKeys.flatMap { listOf(it.position.x, it.position.xRight) })
        private val yCenters = SnappingLines(filteredKeys.map { it.position.yCenter })
        private val ySides = SnappingLines(filteredKeys.flatMap { listOf(it.position.y, it.position.yBottom) })
        private var lineX: Int? = null
        private var lineY: Int? = null

        fun updateMoveSnapX(mouseX: Int) {
            selectedPos.x = mouseX - xOffset
            lineX = with(selectedPos) {
                xCenters.findSnap(xCenter)?.also { xCenter = it }
                    ?: xSides.findSnap(x)?.also { x = it }
                    ?: xSides.findSnap(xRight)?.also { xRight = it }
            }
        }

        fun updateMoveSnapY(mouseY: Int) {
            selectedPos.y = mouseY - yOffset
            lineY = with(selectedPos) {
                yCenters.findSnap(yCenter)?.also { yCenter = it }
                    ?: ySides.findSnap(y)?.also { y = it }
                    ?: ySides.findSnap(yBottom)?.also { yBottom = it }
            }
        }

        override fun VG.draw(mouseX: Int, mouseY: Int) = drawLines(lineX, lineY)
    }

    class Resizing(selectedKeys: ElementUnion) : DraggingState {
        private val draggedPosition = selectedKeys.position
        private val filteredKeys: List<Element> = ModConfig.elements - selectedKeys.elements
        private val xSides = SnappingLines(filteredKeys.flatMap { listOf(it.position.x, it.position.xRight) })
        private val ySides = SnappingLines(filteredKeys.flatMap { listOf(it.position.y, it.position.yBottom) })
        private var lineX: Int? = null
        private var lineY: Int? = null

        fun updateResizeSnapX(mouseX: Int) {
            draggedPosition.width = mouseX - draggedPosition.x

            lineX = with(draggedPosition) {
                xSides.findSnap(xRight)?.also { width = it - x }
            }
        }

        fun updateResizeSnapY(mouseY: Int) {
            draggedPosition.height = mouseY - draggedPosition.y

            lineY = with(draggedPosition) {
                ySides.findSnap(yBottom)?.also { height = it - y }
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
