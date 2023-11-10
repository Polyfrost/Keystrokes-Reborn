package org.polyfrost.polykeystrokes.gui

import cc.polyfrost.oneconfig.utils.dsl.VG
import cc.polyfrost.oneconfig.utils.dsl.drawRect
import cc.polyfrost.oneconfig.utils.dsl.nanoVG
import org.polyfrost.polykeystrokes.config.Element
import org.polyfrost.polykeystrokes.config.ModConfig
import org.polyfrost.polykeystrokes.util.IntRectangle
import org.polyfrost.polykeystrokes.util.MutableRectangle

sealed interface DraggingState {
    fun draw(mouseX: Int, mouseY: Int) = nanoVG(mcScaling = true) {
        draw(mouseX, mouseY)
    }

    fun VG.draw(mouseX: Int, mouseY: Int)
}

class MovingState(
    mouseX: Int, mouseY: Int, selected: Selection,
) : DraggingState {
    private val selectedPos: MutableRectangle = selected.position
    private val filteredKeys: List<Element> = ModConfig.elements - selected.elements
    private val xOffset: Int = mouseX - selectedPos.x
    private val yOffset: Int = mouseY - selectedPos.y
    private val xCenters = SnappingLines(filteredKeys.map { it.position.xCenter })
    private val xSides = SnappingLines(filteredKeys.flatMap { listOf(it.position.x, it.position.xRight) })
    private val yCenters = SnappingLines(filteredKeys.map { it.position.yCenter })
    private val ySides = SnappingLines(filteredKeys.flatMap { listOf(it.position.y, it.position.yBottom) })
    private var lineX: Int? = null
    private var lineY: Int? = null

    fun move(mouseX: Int, mouseY: Int) {
        selectedPos.x = mouseX - xOffset
        lineX = with(selectedPos) {
            xCenters.findSnap(xCenter)?.also { xCenter = it }
                ?: xSides.findSnap(x)?.also { x = it }
                ?: xSides.findSnap(xRight)?.also { xRight = it }
        }
        selectedPos.y = mouseY - yOffset
        lineY = with(selectedPos) {
            yCenters.findSnap(yCenter)?.also { yCenter = it }
                ?: ySides.findSnap(y)?.also { y = it }
                ?: ySides.findSnap(yBottom)?.also { yBottom = it }
        }
    }

    override fun VG.draw(mouseX: Int, mouseY: Int) = drawLines(lineX, lineY)
}

class ResizingState(selection: Selection) : DraggingState {
    private val selectionPos = selection.position
    private val filteredKeys: List<Element> = ModConfig.elements - selection.elements
    private val xSides = SnappingLines(filteredKeys.flatMap { listOf(it.position.x, it.position.xRight) })
    private val ySides = SnappingLines(filteredKeys.flatMap { listOf(it.position.y, it.position.yBottom) })
    private var lineX: Int? = null
    private var lineY: Int? = null

    fun resize(mouseX: Int, mouseY: Int) {
        selectionPos.width = mouseX - selectionPos.x
        lineX = with(selectionPos) {
            xSides.findSnap(xRight)?.also { width = it - x }
        }

        selectionPos.height = mouseY - selectionPos.y
        lineY = with(selectionPos) {
            ySides.findSnap(yBottom)?.also { height = it - y }
        }
    }

    override fun VG.draw(mouseX: Int, mouseY: Int) = drawLines(lineX, lineY)
}

class SelectingState(
    private val clickedMouseX: Int,
    private val clickedMouseY: Int,
) : DraggingState {
    fun getSelection(mouseX: Int, mouseY: Int): Selection {
        val selectionBox = getSelectionBox(mouseX, mouseY)
        return Selection(ModConfig.elements.filter { key ->
            key.position intersects selectionBox
        })
    }

    private fun getSelectionBox(currentMouseX: Int, currentMouseY: Int): IntRectangle {
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

private const val SELECTION_COLOR = 0x640000FF

private fun sortLessToGreater(first: Int, second: Int) =
    if (first > second) second to first
    else first to second
