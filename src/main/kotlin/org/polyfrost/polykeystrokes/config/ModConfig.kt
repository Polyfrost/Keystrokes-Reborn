package org.polyfrost.polykeystrokes.config

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.CustomOption
import cc.polyfrost.oneconfig.config.annotations.HUD
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.core.ConfigUtils
import cc.polyfrost.oneconfig.config.data.Mod
import cc.polyfrost.oneconfig.config.data.ModType
import cc.polyfrost.oneconfig.config.elements.BasicOption
import cc.polyfrost.oneconfig.config.elements.OptionPage
import org.polyfrost.polykeystrokes.PolyKeystrokes
import org.polyfrost.polykeystrokes.gui.LayoutEditor
import java.lang.reflect.Field

val settings get() = ModConfig.keystrokes

object ModConfig : Config(Mod(PolyKeystrokes.NAME, ModType.UTIL_QOL, "/${PolyKeystrokes.MODID}.svg"), "${PolyKeystrokes.MODID}.json") {
    @Transient
    @CustomOption
    private val layoutEditor = true

    @Switch(name = "Mouse Tracker")
    var showMouseTracker = false

    @HUD(name = "Settings")
    var keystrokes = KeystrokesHud()

    var mouse = MouseElement()

    val elements: List<Element>
        get() =
            if (showMouseTracker) {
                keystrokes.keys + mouse
            } else {
                keystrokes.keys
            }

    init {
        initialize()
    }

    override fun getCustomOption(field: Field, annotation: CustomOption, page: OptionPage, mod: Mod, migrate: Boolean): BasicOption? {
        ConfigUtils.getSubCategory(page, "General", "").options.add(LayoutEditor("General", ""))
        return null
    }
}