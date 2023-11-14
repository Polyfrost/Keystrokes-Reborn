package org.polyfrost.polykeystrokes.util

import cc.polyfrost.oneconfig.utils.dsl.mc
import org.lwjgl.input.Mouse

object MouseUtils {
    private var deltaXAnimation = ContinualAnimation()
    private var deltaYAnimation = ContinualAnimation()

    private val mouseDX: Int
        get() = if (mc.inGameHasFocus) {
            mc.mouseHelper.deltaX
        } else {
            Mouse.getDX()
        }

    private val mouseDY: Int
        get() = if (mc.inGameHasFocus) {
            -mc.mouseHelper.deltaY
        } else {
            -Mouse.getDY()
        }

    val smoothedMouseMovement: Pair<Float, Float>
        get() {
            deltaXAnimation.push(mouseDX)
            deltaYAnimation.push(mouseDY)
            return deltaXAnimation.get() to deltaYAnimation.get()
        }
}