package org.polyfrost.polykeystrokes

import cc.polyfrost.oneconfig.libs.universal.UMatrixStack
import net.minecraftforge.client.event.GuiScreenEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.polyfrost.polykeystrokes.config.ModConfig
import org.polyfrost.polykeystrokes.gui.KeyEditorUI

@Mod(
    modid = PolyKeystrokes.MODID,
    name = PolyKeystrokes.NAME,
    version = PolyKeystrokes.VERSION,
    modLanguageAdapter = "cc.polyfrost.oneconfig.utils.KotlinLanguageAdapter"
)
object PolyKeystrokes {
    const val MODID = "@ID@"
    const val NAME = "@NAME@"
    const val VERSION = "@VER@"

    @Mod.EventHandler
    fun onInit(event: FMLInitializationEvent) {
        ModConfig
        MinecraftForge.EVENT_BUS.register(this)
    }

    var pageRendered = false
//    val page = KeyEditorUI()

    @SubscribeEvent
    fun onGui(event: GuiScreenEvent.DrawScreenEvent.Post) {
        if (!pageRendered) return
        pageRendered = false
//        page.draw(event.mouseX, event.mouseY)
    }
}