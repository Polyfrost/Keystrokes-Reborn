package org.polyfrost.polykeystrokes.gui

import cc.polyfrost.oneconfig.gui.pages.Page
import cc.polyfrost.oneconfig.utils.InputHandler
import org.polyfrost.polykeystrokes.config.ModConfig

class LayoutPage : Page("Layout") {
    var dragging: KeyElement? = null
    var xOffset = 0
    var yOffset = 0

    override fun draw(vg: Long, x: Int, y: Int, inputHandler: InputHandler) {
        val relativeMouseX = inputHandler.mouseX().toInt() - x
        val relativeMouseY = inputHandler.mouseY().toInt() - y

        for (key in ModConfig.keystrokes.keys) {
            key.draw(x.toFloat(), y.toFloat(), ModConfig.keystrokes.scale)
            val clicked = inputHandler.isAreaClicked(x.toFloat() + key.x, y.toFloat() + key.y, key.width.toFloat(), key.height.toFloat())
            if (clicked) {
                dragging = key
                xOffset = key.x - relativeMouseX
                yOffset = key.y - relativeMouseY
            }
        }
        if (inputHandler.isMouseDown) {
            dragging?.let { key ->
                key.x = relativeMouseX + xOffset
                key.y = relativeMouseY + yOffset
            }
        } else {
            dragging = null
        }
    }
}