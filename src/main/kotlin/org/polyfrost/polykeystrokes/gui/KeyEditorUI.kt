package org.polyfrost.polykeystrokes.gui

import cc.polyfrost.oneconfig.gui.elements.BasicButton
import cc.polyfrost.oneconfig.gui.pages.Page
import cc.polyfrost.oneconfig.libs.universal.UKeyboard
import cc.polyfrost.oneconfig.libs.universal.UResolution
import cc.polyfrost.oneconfig.utils.InputHandler
import cc.polyfrost.oneconfig.utils.color.ColorPalette
import cc.polyfrost.oneconfig.utils.dsl.drawHollowRoundedRect
import cc.polyfrost.oneconfig.utils.dsl.nanoVG
import org.polyfrost.polykeystrokes.config.Element
import org.polyfrost.polykeystrokes.config.ModConfig.elements
import org.polyfrost.polykeystrokes.util.MouseUtils.isFirstClicked
import org.polyfrost.polykeystrokes.util.TransformedVG

private const val BORDER_COLOR = 0xFFFFFFFF.toInt()

private fun Element.drawEditing() = nanoVG(mcScaling = true) {
    draw(TransformedVG(mcScaling = true))

    drawHollowRoundedRect(
        x = position.x - 1,
        y = position.y - 1,
        width = position.width + 1,
        height = position.height + 1,
        radius = 0,
        color = BORDER_COLOR,
        thickness = 1
    )
}

class KeyEditorUI : Page("key editor?") {
    private var selection: Selection? = null
    private var draggingState: DraggingState? = null

    override fun draw(vg: Long, x: Int, y: Int, inputHandler: InputHandler) {
        for (key in elements) {
            key.drawEditing()
        }

        val mouseX = (inputHandler.mouseX() / UResolution.scaleFactor).toInt()
        val mouseY = (inputHandler.mouseY() / UResolution.scaleFactor).toInt()

        when {
            inputHandler.isFirstClicked -> onClicked(mouseX, mouseY)
            inputHandler.isMouseDown -> onDragged(mouseX, mouseY)
            inputHandler.isClicked -> onReleased()
        }

        selection?.draw()
        draggingState?.draw(mouseX, mouseY)
    }

    private fun onClicked(mouseX: Int, mouseY: Int) {
        draggingState = tryResizing(mouseX, mouseY)
            ?: tryMovingSelection(mouseX, mouseY)
                ?: tryMovingNew(mouseX, mouseY)
                ?: startSelectionBox(mouseX, mouseY)
    }

    private fun tryResizing(mouseX: Int, mouseY: Int): ResizingState? =
        selection?.takeIf { selection ->
            selection.isResizeButtonHovered(mouseX, mouseY)
        }?.let { selection ->
            ResizingState(selection)
        }

    private fun tryMovingSelection(mouseX: Int, mouseY: Int): MovingState? =
        selection?.takeIf { selection ->
            selection.position.contains(mouseX, mouseY)
        }?.let { selection ->
            MovingState(mouseX, mouseY, selection)
        }

    private fun tryMovingNew(mouseX: Int, mouseY: Int): MovingState? =
        elements.firstOrNull { element ->
            element.position.contains(mouseX, mouseY)
        }?.let { clickedElement ->
            Selection(clickedElement)
        }?.let { newSelection ->
            selection = newSelection
            MovingState(mouseX, mouseY, newSelection)
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

    override fun keyTyped(key: Char, keyCode: Int) {
        super.keyTyped(key, keyCode)

        val selection = selection ?: return
        when (keyCode) {
            UKeyboard.KEY_UP -> selection.moveBy(0, -1)
            UKeyboard.KEY_DOWN -> selection.moveBy(0, 1)
            UKeyboard.KEY_LEFT -> selection.moveBy(-1, 0)
            UKeyboard.KEY_RIGHT -> selection.moveBy(1, 0)
        }
    }
}
