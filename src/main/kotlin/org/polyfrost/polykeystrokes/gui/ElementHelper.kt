package org.polyfrost.polykeystrokes.gui

import cc.polyfrost.oneconfig.utils.dsl.drawCircle
import cc.polyfrost.oneconfig.utils.dsl.drawHollowRoundedRect
import cc.polyfrost.oneconfig.utils.dsl.drawRect
import cc.polyfrost.oneconfig.utils.dsl.nanoVG
import org.polyfrost.polykeystrokes.config.Element
import org.polyfrost.polykeystrokes.config.ModConfig

val elements: ElementList get() = ModConfig.keystrokes.elements

typealias ElementList = List<Element>

private const val RESIZE_BUTTON_RADIUS = 4
private const val SCALE_BUTTON_RADIUS_SQUARED = RESIZE_BUTTON_RADIUS * RESIZE_BUTTON_RADIUS
private const val RESIZE_BUTTON_COLOR = 0xC8008080.toInt()

fun Element.isResizeButtonHovered(mouseX: Double, mouseY: Double): Boolean {
    val deltaX = position.xRight - mouseX
    val deltaY = position.yBottom - mouseY
    val distanceSquared = deltaX * deltaX + deltaY * deltaY
    return distanceSquared <= SCALE_BUTTON_RADIUS_SQUARED
}

fun ElementList.moveBy(x: Int, y: Int) {
    for (key in this) {
        key.position.x += x
        key.position.y += y
    }
}

fun ElementList.resizeBy(x: Int, y: Int) {
    for (key in this) {
        key.position.width += x
        key.position.height += y
    }
}


private const val SELECTED_COLOR = 0x3C008080
private const val BORDER_COLOR = 0xFFFFFFFF.toInt()

fun Element.drawEditing(selected: Boolean) = nanoVG(mcScaling = true) {
    draw(0, 0)

    drawHollowRoundedRect(
        x = position.x - 1,
        y = position.y - 1,
        width = position.width + 1,
        height = position.height + 1,
        radius = 0,
        color = BORDER_COLOR,
        thickness = 1
    )

    if (selected) {
        drawRect(
            x = position.x,
            y = position.y,
            width = position.width,
            height = position.height,
            color = SELECTED_COLOR
        )
        drawCircle(
            x = position.xRight,
            y = position.yBottom,
            radius = RESIZE_BUTTON_RADIUS,
            color = RESIZE_BUTTON_COLOR
        )
    }
}