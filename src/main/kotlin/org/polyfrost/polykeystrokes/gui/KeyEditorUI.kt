package org.polyfrost.polykeystrokes.gui

import cc.polyfrost.oneconfig.gui.GuiPause
import cc.polyfrost.oneconfig.gui.OneConfigGui
import cc.polyfrost.oneconfig.libs.universal.UKeyboard
import cc.polyfrost.oneconfig.libs.universal.UKeyboard.allowRepeatEvents
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack
import cc.polyfrost.oneconfig.libs.universal.UScreen
import cc.polyfrost.oneconfig.utils.InputHandler
import cc.polyfrost.oneconfig.utils.gui.GuiUtils
import org.polyfrost.polykeystrokes.config.Element

class KeyEditorUI : UScreen(), GuiPause {
    private var draggingState: DraggingState = DraggingState.None
    private var selectedKeys = emptyList<Element>()
    private val inputHandler = InputHandler()
    override fun onDrawScreen(matrixStack: UMatrixStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawDefaultBackground()

        for (key in elements) {
            key.drawEditing(selected = selectedKeys.contains(key))
        }

        if (inputHandler.isMouseDown)
            onDrag(mouseX, mouseY)

        val drawable = draggingState as? DraggingState.DrawableState
        drawable?.draw(mouseX, mouseY)

        super.onDrawScreen(matrixStack, mouseX, mouseY, partialTicks)
    }

    override fun onMouseClicked(mouseX: Double, mouseY: Double, mouseButton: Int) {
        super.onMouseClicked(mouseX, mouseY, mouseButton)
        if (mouseButton != 0) return

        findResizing(mouseX, mouseY)
            ?: findDragging(mouseX, mouseY)
            ?: startSelectionBox(mouseX, mouseY)
    }

    private fun findResizing(mouseX: Double, mouseY: Double): Unit? {
        val resizing = selectedKeys.firstOrNull { key ->
            key.isResizeButtonHovered(mouseX, mouseY)
        } ?: return null

        draggingState = DraggingState.Resizing(
            mouseX.toInt(), mouseY.toInt(),
            excludeKeys = selectedKeys,
            holding = resizing
        )
        return Unit
    }

    private fun findDragging(mouseX: Double, mouseY: Double): Unit? {
        val dragged = elements.firstOrNull { key ->
            key.position.contains(mouseX.toInt(), mouseY.toInt())
        } ?: return null

        if (dragged !in selectedKeys) {
            if (isCtrlKeyDown()) {
                selectedKeys += dragged
            } else {
                selectedKeys = listOf(dragged)
            }
        }

        draggingState = DraggingState.Dragging(
            mouseX.toInt(), mouseY.toInt(),
            excludeKeys = selectedKeys,
            dragging = dragged
        )
        return Unit
    }

    private fun startSelectionBox(mouseX: Double, mouseY: Double) {
        selectedKeys = emptyList()
        draggingState = DraggingState.Selecting(mouseX.toInt(), mouseY.toInt())
    }

    fun onDrag(x: Int, y: Int) {
        when (val state = draggingState) {
            is DraggingState.Dragging -> {
                val xChange = state.getSnappedXChange(x)
                val yChange = state.getSnappedYChange(y)
                selectedKeys.moveBy(xChange, yChange)
            }

            is DraggingState.Resizing -> {
                val xChange = state.getSnappedXRightChange(x)
                val yChange = state.getSnappedYBottomChange(y)
                selectedKeys.resizeBy(xChange, yChange)
            }

            is DraggingState.Selecting -> {
                val selectionBox = state.getSelectionBox(x, y)
                selectedKeys = elements.filter { key ->
                    key.position intersects selectionBox
                }
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
            UKeyboard.KEY_UP -> selectedKeys.moveBy(0, -1)
            UKeyboard.KEY_DOWN -> selectedKeys.moveBy(0, 1)
            UKeyboard.KEY_LEFT -> selectedKeys.moveBy(-1, 0)
            UKeyboard.KEY_RIGHT -> selectedKeys.moveBy(1, 0)
        }
    }

    override fun initScreen(width: Int, height: Int) {
        super.initScreen(width, height)
        allowRepeatEvents(true)
    }

    override fun onScreenClose() {
        super.onScreenClose()

        allowRepeatEvents(false)
        GuiUtils.displayScreen(OneConfigGui.create())
    }

    override fun doesGuiPauseGame() = false
}
