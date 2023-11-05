package org.polyfrost.polykeystrokes.gui

import java.awt.Rectangle

interface DraggingState {
    object None : DraggingState

    class Dragging(
        var currentMouseX: Int,
        var currentMouseY: Int,
    ) : DraggingState

    class Selecting(
        private val clickedMouseX: Int,
        private val clickedMouseY: Int,
    ) : DraggingState {
        fun getSelectionBox(currentMouseX: Int, currentMouseY: Int): Rectangle {
            val (xLeft, xRight) = swapIfSecondIsLess(clickedMouseX, currentMouseX)
            val (yTop, yBottom) = swapIfSecondIsLess(clickedMouseY, currentMouseY)
            val width = xRight - xLeft
            val height = yBottom - yTop
            return Rectangle(xLeft, yTop, width, height)
        }
    }
}

private fun swapIfSecondIsLess(first: Int, second: Int): Pair<Int, Int> {
    if (first > second) return second to first
    return first to second
}