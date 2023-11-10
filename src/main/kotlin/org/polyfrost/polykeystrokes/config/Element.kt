package org.polyfrost.polykeystrokes.config

import org.polyfrost.polykeystrokes.util.IntRectangle
import org.polyfrost.polykeystrokes.util.VGMatrixStack

interface Element {
    val position: IntRectangle
    fun draw(vg: VGMatrixStack)
}