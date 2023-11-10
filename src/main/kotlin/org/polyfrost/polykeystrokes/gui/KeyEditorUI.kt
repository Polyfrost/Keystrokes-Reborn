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
    private var selection: Selection? = null
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
        selection?.draw()
        draggingState?.draw(mouseX, mouseY)

        super.onDrawScreen(matrixStack, mouseX, mouseY, partialTicks)
    }

    private fun onClicked(mouseX: Int, mouseY: Int) {
        draggingState = (tryResizing(mouseX, mouseY)
            ?: tryMoving(mouseX, mouseY))
            ?: startSelectionBox(mouseX, mouseY)
    }

    private fun tryResizing(mouseX: Int, mouseY: Int): ResizingState? {
        val selection = selection ?: return null
        val hovered = selection.isResizeButtonHovered(mouseX, mouseY)
        if (!hovered) return null
        return ResizingState(selection)
    }

    private fun tryMoving(mouseX: Int, mouseY: Int): MovingState? {
        val singleSelection = elements.firstOrNull { key ->
            key.position.contains(mouseX, mouseY)
        }?.let { Selection(it) } ?: return null

        selection = singleSelection
        return MovingState(mouseX, mouseY, singleSelection)
    }

    private fun startSelectionBox(mouseX: Int, mouseY: Int): SelectingState {
        selection = null
        return SelectingState(mouseX, mouseY)
    }

    private fun onDragged(mouseX: Int, mouseY: Int) = when (val state = draggingState) {
        is ResizingState -> state.resize(mouseX, mouseY)
        is MovingState -> state.move(mouseX, mouseY)
        is SelectingState -> selection = state.getSelection(mouseX, mouseY)
        else -> {}
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
