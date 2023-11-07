package org.polyfrost.polykeystrokes.config

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.Button
import cc.polyfrost.oneconfig.config.annotations.HUD
import cc.polyfrost.oneconfig.config.data.Mod
import cc.polyfrost.oneconfig.config.data.ModType
import cc.polyfrost.oneconfig.libs.universal.UScreen
import org.polyfrost.polykeystrokes.PolyKeystrokes
import org.polyfrost.polykeystrokes.gui.KeyEditorUI

val settings get() = ModConfig.keystrokes

object ModConfig : Config(Mod(PolyKeystrokes.NAME, ModType.UTIL_QOL, "/${PolyKeystrokes.MODID}.svg"), "${PolyKeystrokes.MODID}.json") {

    @HUD(name = "Keystrokes")
    var keystrokes = KeystrokesHud()

    @Button(name = "add key", text = "click")
    fun addKey() {
        keystrokes.elements.add(KeyElement())
    }

    @Button(name = "add mouse", text = "click")
    fun addMouse() {
        keystrokes.elements.add(MouseElement())
    }

    @Button(name = "Layout Menu", text = "Open", size = 2)
    fun openLayoutMenu() {
        UScreen.displayScreen(KeyEditorUI())
    }

    init {
        initialize()
    }
}