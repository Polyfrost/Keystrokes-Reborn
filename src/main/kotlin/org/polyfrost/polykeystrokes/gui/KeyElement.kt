package org.polyfrost.polykeystrokes.gui

import cc.polyfrost.oneconfig.config.core.OneKeyBind

class KeyElement(
    var text: String = "None",
    var keybind: OneKeyBind = OneKeyBind(),
    var xOffset: Int = 0,
    var yOffset: Int = 0,
    var width: Int = 24,
    var height: Int = 24,
)