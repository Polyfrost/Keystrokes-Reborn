package org.polyfrost.polykeystrokes.gui

import cc.polyfrost.oneconfig.gui.pages.Page
import cc.polyfrost.oneconfig.utils.InputHandler
import org.polyfrost.polykeystrokes.config.ModConfig

class LayoutPage : Page("Layout") {
    override fun draw(vg: Long, x: Int, y: Int, inputHandler: InputHandler?) {
        for (key in ModConfig.keystrokes.keys) {
            key.draw(x.toFloat(), y.toFloat(), ModConfig.keystrokes.scale)
        }
    }
}