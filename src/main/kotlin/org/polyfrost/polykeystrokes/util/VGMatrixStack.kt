package org.polyfrost.polykeystrokes.util

import cc.polyfrost.oneconfig.utils.dsl.VG
import cc.polyfrost.oneconfig.utils.dsl.nanoVG

class VGMatrixStack(
    private val mcScaling: Boolean = false,
    private val preTransform: VG.() -> Unit = {}
) {
    fun runApplied(action: VG.() -> Unit) {
        nanoVG(mcScaling) {
            preTransform()
            action()
        }
    }
}