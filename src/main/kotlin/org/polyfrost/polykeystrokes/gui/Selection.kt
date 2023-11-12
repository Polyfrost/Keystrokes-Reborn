package org.polyfrost.polykeystrokes.gui

import cc.polyfrost.oneconfig.utils.dsl.*
import org.polyfrost.polykeystrokes.config.Element
import org.polyfrost.polykeystrokes.util.MutableRectangle
import org.polyfrost.polykeystrokes.util.MutableUnionRectangle

private const val RESIZE_BUTTON_RADIUS = 4
private const val SCALE_BUTTON_RADIUS_SQUARED = RESIZE_BUTTON_RADIUS * RESIZE_BUTTON_RADIUS
private const val RESIZE_BUTTON_COLOR = 0xC8008080.toInt()
private const val SELECTED_COLOR = 0x3C008080

class Selection(
    val selectedElements: Set<Element>,
    val position: MutableRectangle = MutableUnionRectangle(selectedElements.map { it.position }),
) {
    constructor(element: Element) : this(setOf(element), element.position)
    constructor(elements: List<Element>) : this(elements.toSet())

    fun isResizeButtonHovered(mouseX: Int, mouseY: Int): Boolean {
        val xDistance = position.xRight - mouseX
        val yDistance = position.yBottom - mouseY
        val distanceSquared = xDistance * xDistance + yDistance * yDistance
        return distanceSquared <= SCALE_BUTTON_RADIUS_SQUARED
    }

    fun draw(vg: Long) = nanoVG(vg) {
        drawRect(
            x = position.x,
            y = position.y,
            width = position.width,
            height = position.height,
            color = SELECTED_COLOR
        )
        drawHollowRoundedRect(
            x = position.x - 1,
            y = position.y - 1,
            width = position.width + 1,
            height = position.height + 1,
            radius = 0,
            color = RESIZE_BUTTON_COLOR,
            thickness = 1
        )
        drawCircle(
            x = position.xRight,
            y = position.yBottom,
            radius = RESIZE_BUTTON_RADIUS,
            color = RESIZE_BUTTON_COLOR
        )
    }
}