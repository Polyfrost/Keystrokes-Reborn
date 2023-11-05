package org.polyfrost.polykeystrokes.gui

import cc.polyfrost.oneconfig.gui.GuiPause
import cc.polyfrost.oneconfig.gui.OneConfigGui
import cc.polyfrost.oneconfig.libs.universal.UKeyboard
import cc.polyfrost.oneconfig.libs.universal.UKeyboard.allowRepeatEvents
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack
import cc.polyfrost.oneconfig.libs.universal.UScreen
import cc.polyfrost.oneconfig.utils.dsl.drawLine
import cc.polyfrost.oneconfig.utils.dsl.drawRect
import cc.polyfrost.oneconfig.utils.dsl.nanoVG
import cc.polyfrost.oneconfig.utils.gui.GuiUtils
import org.polyfrost.polykeystrokes.config.ModConfig

private const val SNAPPING_DISTANCE = 10
private const val SELECTION_COLOR = 0x640000FF
private val keys get() = ModConfig.keystrokes.keys

class KeyEditorUI : UScreen(), GuiPause {
    private var selected = KeyList()

    override fun initScreen(width: Int, height: Int) {
        super.initScreen(width, height)
        allowRepeatEvents(true)
    }

    override fun onDrawScreen(matrixStack: UMatrixStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawDefaultBackground()

        for (key in keys) {
            key.drawEditing(selected = selected.contains(key))
        }

        super.onDrawScreen(matrixStack, mouseX, mouseY, partialTicks)
    }

    private var draggingState: DraggingState = DraggingState.None

    override fun onMouseClicked(mouseX: Double, mouseY: Double, mouseButton: Int) {
        super.onMouseClicked(mouseX, mouseY, mouseButton)
        if (mouseButton != 0) return


        val keyClicked = keys.firstOrNull { key ->
            key.position.contains(mouseX, mouseY)
        }

        if (keyClicked == null) {
            draggingState = DraggingState.Selecting(mouseX.toInt(), mouseY.toInt())
            return
        }

        selected.clear()
        selected.add(keyClicked)
        draggingState = DraggingState.Dragging(mouseX.toInt(), mouseY.toInt())

    }

    override fun onMouseDragged(x: Double, y: Double, clickedButton: Int, timeSinceLastClick: Long) {
        super.onMouseDragged(x, y, clickedButton, timeSinceLastClick)
        if (clickedButton != 0) return

        when (val state = draggingState) {
            is DraggingState.Dragging -> {
                selected.moveBy((state.currentMouseX - x).toInt(), (state.currentMouseY - y).toInt())
                state.currentMouseX = x.toInt()
                state.currentMouseY = y.toInt()
            }

            is DraggingState.Selecting -> nanoVG(mcScaling = true) {
                val selectionBox = state.getSelectionBox(x.toInt(), y.toInt())
                drawRect(
                    x = selectionBox.x,
                    y = selectionBox.y,
                    width = selectionBox.width,
                    height = selectionBox.height,
                    color = SELECTION_COLOR
                )
                selected = keys.filter { key ->
                    key.position.intersects(selectionBox)
                }.toHashSet()
            }
        }
    }

    override fun onMouseReleased(mouseX: Double, mouseY: Double, state: Int) {
        super.onMouseReleased(mouseX, mouseY, state)
        if (state != 0) return

        draggingState = DraggingState.None
    }

    override fun onKeyPressed(keyCode: Int, typedChar: Char, modifiers: UKeyboard.Modifiers?) {
        super.onKeyPressed(keyCode, typedChar, modifiers)

        when (keyCode) {
            UKeyboard.KEY_UP -> selected.moveBy(0, -1)
            UKeyboard.KEY_DOWN -> selected.moveBy(0, 1)
            UKeyboard.KEY_LEFT -> selected.moveBy(-1, 0)
            UKeyboard.KEY_RIGHT -> selected.moveBy(1, 0)
        }
    }

    override fun onScreenClose() {
        super.onScreenClose()

        allowRepeatEvents(false)
        GuiUtils.displayScreen(OneConfigGui.create())
    }

    override fun doesGuiPauseGame() = false
}

private const val LINE_COLOR = 0xFF8A2BE2.toInt()

class SnappingLine(private val lineCenter: Float, left: Float, size: Float, multipleSides: Boolean) {
    var distance = 0f
    var position = 0f

    init {
        val center = left + size / 2f
        val right = left + size
        val leftDistance = Math.abs(lineCenter - left)
        val centerDistance = Math.abs(lineCenter - center)
        val rightDistance = Math.abs(lineCenter - right)
        if (!multipleSides || leftDistance <= centerDistance && leftDistance <= rightDistance) {
            distance = leftDistance
            position = lineCenter
        } else if (centerDistance <= rightDistance) {
            distance = centerDistance
            position = lineCenter - size / 2f
        } else {
            distance = rightDistance
            position = lineCenter - size
        }
    }

    fun drawLine(screenWidth: Int, screenHeight: Int, lineWidth: Float, isX: Boolean) = nanoVG(mcScaling = true) {
        val lineStart = (lineCenter - lineWidth / 2f)
        if (isX) drawLine(
            x1 = lineStart,
            y1 = 0f,
            x2 = lineStart,
            y2 = screenHeight,
            width = lineWidth,
            color = LINE_COLOR
        ) else drawLine(
            x1 = 0f,
            y1 = lineStart,
            x2 = screenWidth,
            y2 = lineStart,
            width = lineWidth,
            color = LINE_COLOR
        )
    }
}