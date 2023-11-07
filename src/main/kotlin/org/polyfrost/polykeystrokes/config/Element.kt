package org.polyfrost.polykeystrokes.config

import cc.polyfrost.oneconfig.libs.universal.UGraphics.GL
import cc.polyfrost.oneconfig.utils.dsl.*
import org.polyfrost.polykeystrokes.util.IntRectangle

interface Element {
    val position: IntRectangle

    fun draw(xStart: Int, yStart: Int) = nanoVG(mcScaling = true) {
        GL.pushMatrix()
        GL.translate(-xStart.toFloat(), -yStart.toFloat(), 0f)
        translate(-xStart.toFloat(), -yStart.toFloat())
        draw()
        resetTransform()
        GL.popMatrix()
    }

    fun VG.draw()
}