package org.polyfrost.polykeystrokes.gui

import cc.polyfrost.oneconfig.config.annotations.Color
import cc.polyfrost.oneconfig.config.annotations.Dropdown
import cc.polyfrost.oneconfig.config.annotations.Slider
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.core.OneColor
import cc.polyfrost.oneconfig.hud.Hud
import cc.polyfrost.oneconfig.libs.universal.UGraphics
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack
import cc.polyfrost.oneconfig.platform.Platform
import cc.polyfrost.oneconfig.renderer.TextRenderer
import cc.polyfrost.oneconfig.renderer.font.Font
import cc.polyfrost.oneconfig.renderer.font.Fonts
import cc.polyfrost.oneconfig.utils.dsl.*

class KeystrokesHud : Hud(true) {

    @Dropdown(name = "Text Type", options = ["No Shadow", "Shadow", "Full Shadow"])
    var textType = 0

    @Color(name = "Text Color")
    var textColor = OneColor(255, 255, 255, 255)

    @Color(name = "Pressed Text Color")
    var pressedTextColor = OneColor(0, 0, 0, 255)

    @Color(name = "Background Color")
    var backgroundColor = OneColor(0, 0, 0, 120)

    @Color(name = "Pressed Background Color")
    var pressedBackgroundColor = OneColor(255, 255, 255, 120)

    @Switch(name = "Border")
    var border = false

    @Color(name = "Border Color")
    var borderColor = OneColor(0, 0, 0)

    @Slider(name = "Border Thickness", min = 0f, max = 10f)
    var borderSize = 2f

    @Switch(name = "Rounded Corner")
    var roundedCorner = false

    @Slider(name = "Corner radius", min = 0f, max = 10f)
    var cornerRadius = 2f

    var keys = ArrayList<KeyElement>()

    override fun draw(matrices: UMatrixStack, x: Float, y: Float, scale: Float, example: Boolean) {

        val xStart = keys.minOf { it.xOffset }
        val yStart = keys.minOf { it.yOffset }

        for (key in keys) {
            nanoVG(mcScaling = true) {
                translate(x, y)
                scale(scale, scale)
                translate(key.xOffset.toFloat() - xStart, key.yOffset.toFloat() - yStart)
                val radius = if (roundedCorner) cornerRadius else 0
                val color = if (key.keybind.isActive) pressedBackgroundColor else backgroundColor
                drawRoundedRect(0, 0, key.width, key.height, radius, color.rgb)
                if (border)
                    drawHollowRoundedRect(-borderSize, -borderSize, key.width + borderSize, key.height + borderSize, radius, borderColor.rgb, borderSize)

                val textColorX = if (key.keybind.isActive) pressedTextColor else textColor
                drawCenteredText(key.text, 0, 0, textColorX.rgb, 12, Fonts.REGULAR)
                resetTransform()
            }

//            val textColorX = if (key.keybind.isActive) pressedTextColor else textColor
//
//            UGraphics.GL.pushMatrix()
//            UGraphics.GL.translate(x, y, 0f)
//            UGraphics.GL.scale(scale, scale, 1f)
//            when (textType) {
//                0 -> Platform.getGLPlatform().drawText(key.text, key.xOffset.toFloat(), key.yOffset.toFloat(), textColorX.rgb, false)
//                1 -> Platform.getGLPlatform().drawText(key.text, key.xOffset.toFloat(), key.yOffset.toFloat(), textColorX.rgb, true)
//                2 -> TextRenderer.drawBorderedText(key.text, key.xOffset.toFloat(), key.yOffset.toFloat(), textColorX.rgb, 100)
//            }
//            UGraphics.GL.popMatrix()
        }
    }


    override fun getWidth(scale: Float, example: Boolean): Float {
        keys ?: return 0f
        if (keys.isEmpty()) return 0f

        val xStart = keys.minOf { it.xOffset }
        val xEnd = keys.maxOf { it.xOffset + it.width }
        return (xEnd - xStart) * scale
    }

    override fun getHeight(scale: Float, example: Boolean): Float {
        keys ?: return 0f
        if (keys.isEmpty()) return 0f

        val yStart = keys.minOf { it.yOffset }
        val yEnd = keys.maxOf { it.yOffset + it.height }
        return (yEnd - yStart) * scale
    }
}

fun VG.drawCenteredText(text: String, x: Number, y: Number, color: Int, size: Number, font: Font) =
    drawText(text, x.toFloat() - getTextWidth(text, size, font) / 2f, y, color, size, font)