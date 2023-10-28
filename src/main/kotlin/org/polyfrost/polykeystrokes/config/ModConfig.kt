package org.polyfrost.polykeystrokes.config

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.Button
import cc.polyfrost.oneconfig.config.annotations.HUD
import cc.polyfrost.oneconfig.config.annotations.Page
import cc.polyfrost.oneconfig.config.data.Mod
import cc.polyfrost.oneconfig.config.data.ModType
import cc.polyfrost.oneconfig.config.data.PageLocation
import org.polyfrost.polykeystrokes.PolyKeystrokes
import org.polyfrost.polykeystrokes.gui.KeyElement
import org.polyfrost.polykeystrokes.gui.KeystrokesHud
import org.polyfrost.polykeystrokes.gui.LayoutPage

object ModConfig : Config(Mod(PolyKeystrokes.NAME, ModType.UTIL_QOL, "/${PolyKeystrokes.MODID}.svg"), "${PolyKeystrokes.MODID}.json") {

    @HUD(name = "Keystrokes")
    var keystrokes = KeystrokesHud()

    @Button(name = "addKey", text = "click")
    fun addKey() {
        keystrokes.keys.add(KeyElement())
    }

    @Page(name = "Layout", location = PageLocation.TOP)
    var layout = LayoutPage()

    init {
        initialize()
    }
}