package org.polyfrost.polykeystrokes.gui

import cc.polyfrost.oneconfig.utils.dsl.drawCircle
import cc.polyfrost.oneconfig.utils.dsl.drawHollowRoundedRect
import cc.polyfrost.oneconfig.utils.dsl.drawRect
import cc.polyfrost.oneconfig.utils.dsl.nanoVG
import org.polyfrost.polykeystrokes.config.KeyElement
import org.polyfrost.polykeystrokes.utils.xRight
import org.polyfrost.polykeystrokes.utils.yBottom
import java.awt.Point

typealias KeyList = HashSet<KeyElement>

private const val SCALE_BUTTON_RADIUS = 4
private const val SCALE_BUTTON_RADIUS_SQUARED = SCALE_BUTTON_RADIUS * SCALE_BUTTON_RADIUS
private const val SCALE_BUTTON_COLOR = 0xC8008080.toInt()

fun KeyElement.isScaleButtonHovered(mouseX: Double, mouseY: Double): Boolean {
    val distanceToScaleButtonCenter = Point(position.xRight, position.yBottom).distanceSq(mouseX, mouseY)
    return distanceToScaleButtonCenter <= SCALE_BUTTON_RADIUS_SQUARED
}
fun KeyList.moveBy(x: Int, y: Int) {
    for (key in this) {
        key.position.translate(x, y)
    }
}

private const val SELECTED_COLOR = 0x3C008080
private const val BORDER_COLOR = 0xFFFFFFFF.toInt()

fun KeyElement.drawEditing(selected: Boolean) = nanoVG(mcScaling = true) {
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
            radius = SCALE_BUTTON_RADIUS,
            color = SCALE_BUTTON_COLOR
        )
    }
}