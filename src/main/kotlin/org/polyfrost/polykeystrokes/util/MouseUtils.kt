package org.polyfrost.polykeystrokes.util

import net.minecraftforge.client.event.MouseEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object MouseUtils {
    var deltaX: Int = 0
    var deltaY: Int = 0

    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun onMouse(e: MouseEvent) {
        deltaX = e.dx
        deltaY = e.dy
    }
}