package org.polyfrost.polykeystrokes.gui

import cc.polyfrost.oneconfig.utils.dsl.VG
import cc.polyfrost.oneconfig.utils.dsl.drawRect
import cc.polyfrost.oneconfig.utils.dsl.nanoVG
import org.polyfrost.polykeystrokes.config.KeyElement
import java.awt.Rectangle

private const val SELECTION_COLOR = 0x640000FF

interface DraggingState {
    object None : DraggingState

    class Dragging(
        currentMouseX: Int, currentMouseY: Int, excludeKeys: List<KeyElement>, dragging: KeyElement,
    ) : Snapping(currentMouseX, currentMouseY, excludeKeys, dragging)

    class Resizing(
        currentMouseX: Int, currentMouseY: Int, excludeKeys: List<KeyElement>, holding: KeyElement,
    ) : Snapping(currentMouseX, currentMouseY, excludeKeys, holding)

    class Selecting(
        private val clickedMouseX: Int,
        private val clickedMouseY: Int,
    ) : DrawableState {
        fun getSelectionBox(currentMouseX: Int, currentMouseY: Int): Rectangle {
            val (xLeft, xRight) = sortLessToGreater(clickedMouseX, currentMouseX)
            val (yTop, yBottom) = sortLessToGreater(clickedMouseY, currentMouseY)
            val width = xRight - xLeft
            val height = yBottom - yTop
            return Rectangle(xLeft, yTop, width, height)
        }

        override fun VG.draw(mouseX: Int, mouseY: Int) = with(getSelectionBox(mouseX, mouseY)) {
            drawRect(x, y, width, height, SELECTION_COLOR)
        }
    }

    interface DrawableState : DraggingState {
        fun draw(mouseX: Int, mouseY: Int) = nanoVG(mcScaling = true) {
            draw(mouseX, mouseY)
        }

        fun VG.draw(mouseX: Int, mouseY: Int)
    }
}

private fun sortLessToGreater(first: Int, second: Int) =
    if (first > second) second to first
    else first to second
