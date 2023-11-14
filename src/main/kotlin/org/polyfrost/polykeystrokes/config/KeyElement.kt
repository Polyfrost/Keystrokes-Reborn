package org.polyfrost.polykeystrokes.config

import cc.polyfrost.oneconfig.config.annotations.KeyBind
import cc.polyfrost.oneconfig.config.annotations.Text
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.gui.animations.EaseInOutQuad
import cc.polyfrost.oneconfig.renderer.font.Font
import cc.polyfrost.oneconfig.renderer.font.Fonts
import cc.polyfrost.oneconfig.utils.color.ColorUtils
import cc.polyfrost.oneconfig.utils.dsl.*
import org.polyfrost.polykeystrokes.util.IntRectangle

class KeyElement : Element {
    @Text(name = "Text")
    var text = "None"

    @KeyBind(name = "Key")
    var keybind = OneKeyBind()

    override var position = IntRectangle(0, 0, 24, 24)

    @Transient
    var downPercentage = EaseInOutQuad(0, 0f, 0f, false)

    private fun updatePressPercentage(keyDown: Boolean) {
        if (downPercentage.isReversed == keyDown) return
        downPercentage = EaseInOutQuad(ModConfig.keystrokes.fadeTime, 1f, 0f, keyDown)
    }

    override fun VG.draw() {
        updatePressPercentage(keybind.isActive)
        val radius = if (settings.roundedCorner) settings.cornerRadius else 0
        val backgroundColor = fadeColor(
            from = settings.backgroundColor.rgb,
            to = settings.pressedBackgroundColor.rgb,
            percentage = downPercentage.get()
        )
        val textColor = fadeColor(
            from = settings.textColor.rgb,
            to = settings.pressedTextColor.rgb,
            percentage = downPercentage.get()
        )
        drawRoundedRect(
            x = position.x,
            y = position.y,
            width = position.width,
            height = position.height,
            radius = radius,
            color = backgroundColor
        )
        if (settings.border) drawHollowRoundedRect(
            x = position.x - settings.borderSize,
            y = position.y - settings.borderSize,
            width = position.width + settings.borderSize,
            height = position.height + settings.borderSize,
            radius = radius,
            color = settings.borderColor.rgb,
            thickness = settings.borderSize
        )
        drawCenteredText(
            text = text,
            x = position.xCenterFloat,
            y = position.yCenterFloat + 1f,
            color = textColor,
            size = 12,
            font = Fonts.MEDIUM
        )
    }
}

private fun fadeColor(from: Int, to: Int, percentage: Float): Int = ColorUtils.getColor(
    fade(ColorUtils.getRed(from), ColorUtils.getRed(to), percentage),
    fade(ColorUtils.getGreen(from), ColorUtils.getGreen(to), percentage),
    fade(ColorUtils.getBlue(from), ColorUtils.getBlue(to), percentage),
    fade(ColorUtils.getAlpha(from), ColorUtils.getAlpha(to), percentage),
)

private fun fade(from: Int, to: Int, percentage: Float): Int =
    (from * (1f - percentage) + to * percentage).toInt()

private fun VG.drawCenteredText(text: String, x: Number, y: Number, color: Int, size: Int, font: Font) =
    drawText(text, x.toFloat() - getTextWidth(text, size, font) / 2f, y, color, size, font)
