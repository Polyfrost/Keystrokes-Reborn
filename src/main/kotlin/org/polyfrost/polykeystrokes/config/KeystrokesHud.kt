package org.polyfrost.polykeystrokes.config

import cc.polyfrost.oneconfig.config.annotations.Color
import cc.polyfrost.oneconfig.config.annotations.Dropdown
import cc.polyfrost.oneconfig.config.annotations.Slider
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.core.OneColor
import cc.polyfrost.oneconfig.hud.Hud
import cc.polyfrost.oneconfig.libs.universal.UGraphics
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack
import cc.polyfrost.oneconfig.utils.dsl.*
import org.polyfrost.polykeystrokes.util.IntRectangle

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

    var elements = ArrayList<Element>()

    @Suppress("USELESS_ELVIS") // getWidth and getHeight are called before keys init'd :skull:
    val box: IntRectangle?
        get() {
            elements ?: return null

            if (elements.isEmpty()) return null

            val xLeft = elements.minOf { key ->
                key.position.x
            }
            val yTop = elements.minOf { key ->
                key.position.y
            }
            val xRight = elements.maxOf { key ->
                key.position.xRight
            }
            val yBottom = elements.maxOf { key ->
                key.position.yBottom
            }
            return IntRectangle(xLeft, yTop, xRight - xLeft, yBottom - yTop)
        }

    override fun draw(matrices: UMatrixStack, x: Float, y: Float, scale: Float, example: Boolean) {
        val keystrokesBox = box ?: return

        nanoVG(mcScaling = true) {
            UGraphics.GL.pushMatrix()
            UGraphics.GL.translate(x, y, 0f)
            UGraphics.GL.scale(scale, scale, 1f)
            translate(x, y)
            scale(scale, scale)

            for (key in elements) {
                key.draw(keystrokesBox.x, keystrokesBox.y)
            }

            resetTransform()
            UGraphics.GL.popMatrix()
        }
    }

    override fun getWidth(scale: Float, example: Boolean): Float {
        val width = box?.width ?: return 0f
        return width * scale
    }

    override fun getHeight(scale: Float, example: Boolean): Float {
        val height = box?.height ?: return 0f
        return height * scale
    }
}