package org.polyfrost.polykeystrokes.config

import cc.polyfrost.oneconfig.config.annotations.Slider
import cc.polyfrost.oneconfig.config.core.OneColor
import cc.polyfrost.oneconfig.utils.dsl.VG
import cc.polyfrost.oneconfig.utils.dsl.drawCircle
import cc.polyfrost.oneconfig.utils.dsl.drawHollowRoundedRect
import cc.polyfrost.oneconfig.utils.dsl.drawRoundedRect
import org.polyfrost.polykeystrokes.util.IntRectangle
import org.polyfrost.polykeystrokes.util.MouseUtils

class MouseElement : Element {
    override var position = IntRectangle(0, 0, 22, 22)

    var circleColor = OneColor(255, 255, 255, 255)
    val circleRadius = 5f

    @Slider(name = "Sensitivity", min = 1f, max = 100f)
    var speed = 2f

    override fun VG.draw() {
        val cornerRadius = if (settings.roundedCorner) settings.cornerRadius else 0
        val sensitivity = speed / 100f
        val xOffset = (MouseUtils.deltaX * sensitivity).coerceIn(-0.5f, 0.5f) * position.width
        val yOffset = (MouseUtils.deltaY * sensitivity).coerceIn(-0.5f, 0.5f) * position.height

        drawRoundedRect(
            x = position.x,
            y = position.y,
            width = position.width,
            height = position.height,
            radius = cornerRadius,
            color = settings.backgroundColor.rgb
        )

        if (settings.border) drawHollowRoundedRect(
            x = position.x - settings.borderSize,
            y = position.y - settings.borderSize,
            width = position.width + settings.borderSize,
            height = position.height + settings.borderSize,
            radius = cornerRadius,
            color = settings.borderColor.rgb,
            thickness = settings.borderSize
        )

        drawCircle(
            x = position.xCenter + xOffset,
            y = position.yCenter + yOffset,
            radius = circleRadius,
            color = circleColor.rgb
        )
    }
}