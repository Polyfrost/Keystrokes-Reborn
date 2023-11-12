package org.polyfrost.polykeystrokes.config

import cc.polyfrost.oneconfig.config.annotations.Color
import cc.polyfrost.oneconfig.config.annotations.Slider
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.core.OneColor
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.hud.Hud
import cc.polyfrost.oneconfig.libs.universal.UKeyboard
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack
import cc.polyfrost.oneconfig.utils.dsl.nanoVG
import cc.polyfrost.oneconfig.utils.dsl.scale
import cc.polyfrost.oneconfig.utils.dsl.translate
import org.polyfrost.polykeystrokes.config.ModConfig.elements
import org.polyfrost.polykeystrokes.util.Rectangle
import org.polyfrost.polykeystrokes.util.UnionRectangle

class KeystrokesHud : Hud(true) {
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

    @Slider(name = "Fade Time (milliseconds)", min = 0f, max = 1000f)
    var fadeTime = 500

    var keys: ArrayList<KeyElement> = arrayListOf(
        KeyElement().apply {
            text = "W"
            keybind = OneKeyBind(UKeyboard.KEY_W)
            position.x = 106
            position.y = 18
            position.width = 32
            position.height = 32
        },
        KeyElement().apply {
            text = "A"
            keybind = OneKeyBind(UKeyboard.KEY_A)
            position.x = 72
            position.y = 52
            position.width = 32
            position.height = 32
        },
        KeyElement().apply {
            text = "S"
            keybind = OneKeyBind(UKeyboard.KEY_S)
            position.x = 106
            position.y = 52
            position.width = 32
            position.height = 32
        },
        KeyElement().apply {
            text = "D"
            keybind = OneKeyBind(UKeyboard.KEY_D)
            position.x = 140
            position.y = 52
            position.width = 32
            position.height = 32
        },
        KeyElement().apply {
            text = "LMB"
            keybind = OneKeyBind(-100)
            position.x = 72
            position.y = 86
            position.width = 49
            position.height = 26
        },
        KeyElement().apply {
            text = "RMB"
            keybind = OneKeyBind(-99)
            position.x = 123
            position.y = 86
            position.width = 49
            position.height = 26
        },
        KeyElement().apply {
            text = "SPACE"
            keybind = OneKeyBind(UKeyboard.KEY_SPACE)
            position.x = 72
            position.y = 114
            position.width = 100
            position.height = 16
        },
    )

    @Suppress("USELESS_ELVIS") // getWidth and getHeight are called before keys init'd :skull:
    private val box: Rectangle?
        get() {
            keys ?: return null
            if (elements.isEmpty()) return null
            return UnionRectangle(elements.map { it.position })
        }

    override fun draw(matrices: UMatrixStack, x: Float, y: Float, scale: Float, example: Boolean) {
        val keystrokesBox = box ?: return

        for (key in elements) nanoVG(mcScaling = true) {
            translate(x, y)
            scale(scale, scale)
            translate(-keystrokesBox.x.toFloat(), -keystrokesBox.y.toFloat())
            key.run { draw() }
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