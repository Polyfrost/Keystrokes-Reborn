package org.polyfrost.polykeystrokes.config

import cc.polyfrost.oneconfig.config.annotations.Color
import cc.polyfrost.oneconfig.config.annotations.Dropdown
import cc.polyfrost.oneconfig.config.annotations.Slider
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.core.OneColor
import cc.polyfrost.oneconfig.hud.Hud
import cc.polyfrost.oneconfig.libs.universal.UGraphics.GL
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack
import cc.polyfrost.oneconfig.utils.dsl.scale
import cc.polyfrost.oneconfig.utils.dsl.translate
import org.polyfrost.polykeystrokes.config.ModConfig.elements
import org.polyfrost.polykeystrokes.util.Rectangle
import org.polyfrost.polykeystrokes.util.UnionRectangle
import org.polyfrost.polykeystrokes.util.TransformedVG

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

    @Suppress("USELESS_ELVIS") // getWidth and getHeight are called before keys init'd :skull:
    private val box: Rectangle?
        get() {
            keys ?: return null
            if (elements.isEmpty()) return null
            return UnionRectangle(elements.map { it.position })
        }

    override fun draw(matrices: UMatrixStack, x: Float, y: Float, scale: Float, example: Boolean) {
        val keystrokesBox = box ?: return

        GL.pushMatrix()
        GL.translate(x, y, 0f)
        GL.scale(scale, scale, 1f)
        GL.translate(-keystrokesBox.x.toFloat(), -keystrokesBox.y.toFloat(), 0f)

        val transformedVg = TransformedVG(mcScaling = true) {
            translate(x, y)
            scale(scale, scale)
            translate(-keystrokesBox.x.toFloat(), -keystrokesBox.y.toFloat())
        }

        for (key in elements) {
            key.draw(transformedVg)
        }

        GL.popMatrix()
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