package org.polyfrost.polykeystrokes.config

import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.platform.GLPlatform
import cc.polyfrost.oneconfig.platform.Platform
import cc.polyfrost.oneconfig.renderer.TextRenderer
import cc.polyfrost.oneconfig.utils.dsl.drawHollowRoundedRect
import cc.polyfrost.oneconfig.utils.dsl.drawRoundedRect
import org.polyfrost.polykeystrokes.util.IntRectangle
import org.polyfrost.polykeystrokes.util.VGMatrixStack

class KeyElement : Element {
    var text = "None"
    var keybind = OneKeyBind()
    override var position = IntRectangle(0, 0, 24, 24)

    override fun draw(vg: VGMatrixStack) {
        val radius = if (settings.roundedCorner) settings.cornerRadius else 0
        val backgroundColor = if (keybind.isActive) settings.pressedBackgroundColor else settings.backgroundColor

        vg.runApplied {
            drawRoundedRect(
                x = position.x,
                y = position.y,
                width = position.width,
                height = position.height,
                radius = radius,
                color = backgroundColor.rgb
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
        }

        val textColor = if (keybind.isActive) settings.pressedTextColor else settings.textColor

        Platform.getGLPlatform().drawCenteredText(
            text = text,
            x = position.xCenter,
            y = position.yCenter - 4,
            color = textColor.rgb,
            textType = settings.textType
        )
    }
}

private fun GLPlatform.drawCenteredText(text: String, x: Number, y: Number, color: Int, textType: Int) {
    val shiftedX = x.toFloat() - getStringWidth(text) / 2f

    when (textType) {
        0 -> drawText(text, shiftedX, y.toFloat(), color, false)
        1 -> drawText(text, shiftedX, y.toFloat(), color, true)
        2 -> TextRenderer.drawBorderedText(text, shiftedX, y.toFloat(), color, 100)
    }
}