package org.polyfrost.polykeystrokes.gui

import cc.polyfrost.oneconfig.gui.GuiPause
import cc.polyfrost.oneconfig.gui.OneConfigGui
import cc.polyfrost.oneconfig.libs.universal.UKeyboard
import cc.polyfrost.oneconfig.libs.universal.UKeyboard.allowRepeatEvents
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack
import cc.polyfrost.oneconfig.libs.universal.UResolution
import cc.polyfrost.oneconfig.libs.universal.UScreen
import cc.polyfrost.oneconfig.utils.InputHandler
import cc.polyfrost.oneconfig.utils.gui.GuiUtils
import org.polyfrost.polykeystrokes.config.ModConfig.elements
import org.polyfrost.polykeystrokes.util.MouseUtils.isFirstClicked

class KeyEditorUI : UScreen(), GuiPause {
    private val inputHandler = InputHandler()
    private var selection: ElementUnion? = null
    private var draggingState: DraggingState? = null

    init {
        inputHandler.scale(UResolution.scaleFactor, UResolution.scaleFactor)
    }

    override fun onDrawScreen(matrixStack: UMatrixStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawDefaultBackground()

        for (key in elements) {
            key.drawEditing()
        }

        when {
            inputHandler.isFirstClicked -> onClicked(mouseX, mouseY)
            inputHandler.isMouseDown -> onDragged(mouseX, mouseY)
            inputHandler.isClicked -> onReleased()
        }

        draggingState?.draw(mouseX, mouseY)
        selection?.draw()

        super.onDrawScreen(matrixStack, mouseX, mouseY, partialTicks)
    }

    private fun onClicked(mouseX: Int, mouseY: Int) {
        draggingState = (findResizing(mouseX, mouseY)
            ?: findDragging(mouseX, mouseY))
            ?: startSelectionBox(mouseX, mouseY)
    }

    private fun findResizing(mouseX: Int, mouseY: Int): DraggingState? {
        val selection = selection ?: return null
        val hovered = selection.isResizeButtonHovered(mouseX, mouseY)
        if (!hovered) return null
        return DraggingState.Resizing(selection)
    }

    private fun findDragging(mouseX: Int, mouseY: Int): DraggingState? {
        val clicked = elements.firstOrNull { key ->
            key.position.contains(mouseX, mouseY)
        } ?: return null

        val newSelection = ElementUnion(clicked)
        selection = newSelection
        return DraggingState.Dragging(mouseX, mouseY, newSelection)
    }

    private fun startSelectionBox(mouseX: Int, mouseY: Int): DraggingState {
        selection = null
        return DraggingState.Selecting(mouseX, mouseY)
    }

    private fun onDragged(mouseX: Int, mouseY: Int) {
        when (val state = draggingState) {
            is DraggingState.Dragging -> {
                state.updateMoveSnapX(mouseX)
                state.updateMoveSnapY(mouseY)
            }

            is DraggingState.Resizing -> {
                state.updateResizeSnapX(mouseX)
                state.updateResizeSnapY(mouseY)
            }

            is DraggingState.Selecting -> {
                val selectionBox = state.getSelectionBox(mouseX, mouseY)
                selection = ElementUnion(elements.filter { key ->
                    key.position intersects selectionBox
                }.toSet())
            }
        }
    }

    private fun onReleased() {
        draggingState = null
    }

    override fun onKeyPressed(keyCode: Int, typedChar: Char, modifiers: UKeyboard.Modifiers?) {
        super.onKeyPressed(keyCode, typedChar, modifiers)

        val selection = selection ?: return

        when (keyCode) {
            UKeyboard.KEY_UP -> selection.moveBy(0, -1)
            UKeyboard.KEY_DOWN -> selection.moveBy(0, 1)
            UKeyboard.KEY_LEFT -> selection.moveBy(-1, 0)
            UKeyboard.KEY_RIGHT -> selection.moveBy(1, 0)
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
