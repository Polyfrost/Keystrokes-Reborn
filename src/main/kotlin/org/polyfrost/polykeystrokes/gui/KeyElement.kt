package org.polyfrost.polykeystrokes.gui

import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.renderer.TextRenderer
import cc.polyfrost.oneconfig.utils.dsl.*
import org.polyfrost.polykeystrokes.config.ModConfig

class KeyElement {
    var text = "None"
    var keybind = OneKeyBind()
    var x = 0
    var y = 0
    var width = 24
    var height = 24

    fun draw(xOffset: Float, yOffset: Float, scale: Float) = ModConfig.keystrokes.run {
        nanoVG {
            translate(xOffset + x, yOffset + y)
            scale(scale, scale)
            val radius = if (roundedCorner) cornerRadius else 0
            val color = if (keybind.isActive) pressedBackgroundColor else backgroundColor
            drawRoundedRect(0, 0, width, height, radius, color.rgb)
            if (border)
                drawHollowRoundedRect(-borderSize, -borderSize, width + borderSize, height + borderSize, radius, borderColor.rgb, borderSize)
            resetTransform()
        }
        val textColorX = if (keybind.isActive) pressedTextColor else textColor
        TextRenderer.drawScaledString(text, xOffset + x.toFloat(), yOffset + y.toFloat(), textColorX.rgb, TextRenderer.TextType.toType(textType), scale)
    }

}