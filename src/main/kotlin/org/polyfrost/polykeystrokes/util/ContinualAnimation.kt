package org.polyfrost.polykeystrokes.util

import cc.polyfrost.oneconfig.gui.animations.EaseInOutQuad

class ContinualAnimation(initial: Number = 0) {
    private var animation = EaseInOutQuad(0, initial.toFloat(), initial.toFloat(), false)

    fun push(value: Number) {
        animation = EaseInOutQuad(10, animation.get(), value.toFloat(), false)
    }

    fun get() = animation.get()
}