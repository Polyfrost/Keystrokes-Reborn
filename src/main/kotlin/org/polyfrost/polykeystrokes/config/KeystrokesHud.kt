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
import org.polyfrost.polykeystrokes.utils.unionBox

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
        val keystrokesBox = keys.unionBox ?: return

        nanoVG(mcScaling = true) {
            UGraphics.GL.pushMatrix()
            UGraphics.GL.translate(x, y, 0f)
            UGraphics.GL.scale(scale, scale, 1f)
            translate(x, y)
            scale(scale, scale)

            for (key in keys) {
                key.draw(keystrokesBox.x, keystrokesBox.y)
            }

            resetTransform()
            UGraphics.GL.popMatrix()
        }
    }

    @Suppress("UNNECESSARY_SAFE_CALL") // getWidth is called before keys init'd :skull:
    override fun getWidth(scale: Float, example: Boolean): Float {
        val width = keys?.unionBox?.width ?: return 0f
        return width * scale
    }

    @Suppress("UNNECESSARY_SAFE_CALL") // getHeight is called before keys init'd :skull:
    override fun getHeight(scale: Float, example: Boolean): Float {
        val height = keys?.unionBox?.height ?: return 0f
        return height * scale
    }
}