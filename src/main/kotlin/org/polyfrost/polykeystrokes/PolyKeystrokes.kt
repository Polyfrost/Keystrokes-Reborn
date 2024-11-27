package org.polyfrost.polykeystrokes

import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import org.polyfrost.polykeystrokes.config.ModConfig

@Mod(
    modid = PolyKeystrokes.MODID,
    name = PolyKeystrokes.NAME,
    version = PolyKeystrokes.VERSION,
    modLanguageAdapter = "cc.polyfrost.oneconfig.utils.KotlinLanguageAdapter"
)
object PolyKeystrokes {
    const val MODID = "@MOD_ID@"
    const val NAME = "@MOD_NAME@"
    const val VERSION = "@MOD_VERSION@"

    @Mod.EventHandler
    fun onInit(event: FMLInitializationEvent) {
        ModConfig
    }
}