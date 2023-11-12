package org.polyfrost.polykeystrokes.config

import cc.polyfrost.oneconfig.utils.dsl.VG
import org.polyfrost.polykeystrokes.util.IntRectangle

interface Element {
    val position: IntRectangle
    fun VG.draw()
}