package org.polyfrost.polykeystrokes.config

import org.polyfrost.polykeystrokes.util.IntRectangle
import org.polyfrost.polykeystrokes.util.TransformedVG

interface Element {
    val position: IntRectangle
    fun draw(vg: TransformedVG)
}