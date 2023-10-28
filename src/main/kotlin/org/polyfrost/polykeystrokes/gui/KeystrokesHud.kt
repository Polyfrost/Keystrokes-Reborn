package org.polyfrost.polykeystrokes.gui

import cc.polyfrost.oneconfig.config.annotations.Color
import cc.polyfrost.oneconfig.config.annotations.Dropdown
import cc.polyfrost.oneconfig.config.annotations.Slider
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.core.OneColor
import cc.polyfrost.oneconfig.hud.Hud
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack

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
        for (key in keys) {
            key.draw(x, y, scale)
        }
    }

    override fun getWidth(scale: Float, example: Boolean): Float {
        keys ?: return 0f
        if (keys.isEmpty()) return 0f
        return keys.maxOf { key ->
            key.x + key.width
        } - keys.minOf { key ->
            key.x
        }.toFloat()
    }

    override fun getHeight(scale: Float, example: Boolean): Float {
        keys ?: return 0f
        if (keys.isEmpty()) return 0f
        return keys.maxOf { key ->
            key.y + key.height
        } - keys.minOf { key ->
            key.y
        }.toFloat()
    }
}