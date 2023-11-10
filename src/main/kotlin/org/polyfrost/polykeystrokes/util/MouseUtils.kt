package org.polyfrost.polykeystrokes.util

import cc.polyfrost.oneconfig.events.EventManager
import cc.polyfrost.oneconfig.events.event.RenderEvent
import cc.polyfrost.oneconfig.events.event.Stage
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe
import cc.polyfrost.oneconfig.platform.Platform
import cc.polyfrost.oneconfig.utils.InputHandler
import cc.polyfrost.oneconfig.utils.gui.GuiUtils

object MouseUtils {
    var deltaX = 0
        private set
    var deltaY = 0
        private set

    private var lastX = 0
    private var lastY = 0

    init {
        EventManager.INSTANCE.register(this)
    }

    @Suppress("unused")
    @Subscribe
    fun onMouse(event: RenderEvent) {
        if (event.stage == Stage.END) return
        val mouseX = Platform.getMousePlatform().mouseX.toInt()
        val mouseY = Platform.getMousePlatform().mouseY.toInt()
        deltaX = mouseX - lastX
        deltaY = mouseY - lastY
        lastX = mouseX
        lastY = mouseY
    }

    val InputHandler.isFirstClicked get() = !GuiUtils.wasMouseDown() && isMouseDown
}