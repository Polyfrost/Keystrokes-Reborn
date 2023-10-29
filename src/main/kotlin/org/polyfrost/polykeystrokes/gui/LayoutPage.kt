package org.polyfrost.polykeystrokes.gui

import cc.polyfrost.oneconfig.gui.pages.Page
import cc.polyfrost.oneconfig.renderer.font.Fonts
import cc.polyfrost.oneconfig.utils.InputHandler
import cc.polyfrost.oneconfig.utils.dsl.*
import org.polyfrost.polykeystrokes.config.ModConfig

class LayoutPage : Page("Layout") {
    var dragging: KeyElement? = null
    var holdXOffset = 0
    var holdYOffset = 0

    override fun draw(vg: Long, x: Int, y: Int, inputHandler: InputHandler) {
        val relativeMouseX = inputHandler.mouseX().toInt() - x
        val relativeMouseY = inputHandler.mouseY().toInt() - y

        for (key in ModConfig.keystrokes.keys) {
            val clicked = inputHandler.isAreaHovered(x.toFloat() + key.xOffset, y.toFloat() + key.yOffset, key.width.toFloat(), key.height.toFloat())
            if (clicked) {
                dragging = key
                holdXOffset = key.xOffset - relativeMouseX
                holdYOffset = key.yOffset - relativeMouseY
            }
//            key.draw(x.toFloat(), y.toFloat(), ModConfig.keystrokes.scale)
            ModConfig.keystrokes.apply {
                nanoVG(vg) {
//                val ccolor = if (key == dragging) -1 else 0x20FFFFFF
//                drawRect(x.toFloat() + key.xOffset, y.toFloat() + key.yOffset, key.width.toFloat(), key.height.toFloat(), ccolor)
                    translate(x.toFloat() + key.xOffset, y.toFloat() + key.yOffset)
                    val radius = if (roundedCorner) cornerRadius else 0
                    val color = if (key.keybind.isActive) pressedBackgroundColor else backgroundColor
                    drawRoundedRect(0, 0, key.width, key.height, radius, color.rgb)
                    if (border)
                        drawHollowRoundedRect(-borderSize, -borderSize, key.width + borderSize, key.height + borderSize, radius, borderColor.rgb, borderSize)

                    val textColorX = if (key.keybind.isActive) pressedTextColor else textColor
                    drawCenteredText(key.text, 0, 0, textColorX.rgb, 12, Fonts.REGULAR)
                    resetTransform()
                }
            }
        }
        if (inputHandler.isMouseDown) {
            dragging?.let { key ->
                key.xOffset = relativeMouseX + holdXOffset
                key.yOffset = relativeMouseY + holdYOffset
            }
        } else {
            dragging = null
        }
    }
}