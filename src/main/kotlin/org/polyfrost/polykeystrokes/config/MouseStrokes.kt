package org.polyfrost.polykeystrokes.config

import cc.polyfrost.oneconfig.config.annotations.Color
import cc.polyfrost.oneconfig.config.annotations.Exclude
import cc.polyfrost.oneconfig.config.annotations.Slider
import cc.polyfrost.oneconfig.config.core.OneColor
import cc.polyfrost.oneconfig.events.EventManager
import cc.polyfrost.oneconfig.events.event.RenderEvent
import cc.polyfrost.oneconfig.events.event.Stage
import cc.polyfrost.oneconfig.hud.BasicHud
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack
import cc.polyfrost.oneconfig.utils.dsl.drawCircle
import cc.polyfrost.oneconfig.utils.dsl.nanoVG
import net.minecraft.client.Minecraft
import net.minecraft.util.MathHelper
import org.lwjgl.input.Mouse

class MouseStrokes : BasicHud() {

    init {
        EventManager.INSTANCE.register(this)
    }

    @Transient
    val mc: Minecraft = Minecraft.getMinecraft()

    @Slider(name = "Width", min = 10f, max = 100f)
    var width = 50f

    @Slider(name = "Height", min = 10f, max = 100f)
    var height = 50f

    @Slider(name = "Sensitivity", min = 1f, max = 100f)
    var speed = 20f

    @Slider(name = "Reset Time", description = "reset position when not moving mouse", min = 0f, max = 2000f)
    var resetTime = 300f

    @Color(name = "Cursor Color")
    var color = OneColor(-0x1)

    @Subscribe
    private fun tick(e: RenderEvent) {
        if (e.stage == Stage.END) return
        if (!shouldShow()) return
        val sens = speed / 100f
        val mouseX = (if (mc.currentScreen == null) mc.mouseHelper.deltaX else Mouse.getDX()).toFloat()
        val mouseY = (if (mc.currentScreen == null) mc.mouseHelper.deltaY else Mouse.getDY()).toFloat()
        if (mouseX == 0f && mouseY == 0f) {
            if (Minecraft.getSystemTime() - currentTime > resetTime) {
                sumX = 0f
                sumY = 0f
            }
        } else {
            currentTime = Minecraft.getSystemTime()
        }
        if (sumX + mouseX * sens < -width / 2) {
            sumX = -width / 2
        } else if (sumX + mouseX * sens > width / 2) {
            sumX = width / 2
        } else {
            sumX += mouseX * sens
        }
        if (sumY - mouseY * sens < -height / 2) {
            sumY = -height / 2
        } else if (sumY - mouseY * sens > height / 2) {
            sumY = height / 2
        } else {
            sumY -= mouseY * sens
        }
    }

    override fun draw(matrices: UMatrixStack, x: Float, y: Float, scale: Float, example: Boolean) {
        nanoVG(mcScaling = true) {
            drawCircle(
                x + MathHelper.clamp_float(
                    width / 2 + sumX,
                    0f,
                    width
                ),
                y + MathHelper.clamp_float(
                    height / 2 + sumY,
                    0f,
                    height
                ),
                2f,
                color.rgb
            )
        }
    }

    override fun getWidth(scale: Float, example: Boolean): Float {
        return width
    }

    override fun getHeight(scale: Float, example: Boolean): Float {
        return height
    }

    @Exclude
    companion object {
        @Exclude
        private var sumX = 0f

        @Exclude
        private var sumY = 0f

        @Exclude
        private var currentTime: Long = 0
    }
}