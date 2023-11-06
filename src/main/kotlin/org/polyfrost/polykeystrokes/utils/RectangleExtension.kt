package org.polyfrost.polykeystrokes.utils

import org.polyfrost.polykeystrokes.config.KeyElement
import java.awt.Rectangle

val Rectangle.xCenter get() = x + width / 2
val Rectangle.xRight get() = x + width
val Rectangle.yCenter get() = y + height / 2
val Rectangle.yBottom get() = y + height

val List<KeyElement>.unionBox: Rectangle?
    get() {
        if (isEmpty()) return null

        val xLeft = minOf { key ->
            key.position.x
        }
        val yTop = minOf { key ->
            key.position.y
        }
        val xRight = maxOf { key ->
            key.position.xRight
        }
        val yBottom = maxOf { key ->
            key.position.yBottom
        }
        return Rectangle(xLeft, yTop, xRight - xLeft, yBottom - yTop)
    }