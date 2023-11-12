@file:Suppress("UnstableApiUsage")

package org.polyfrost.polykeystrokes.gui

import cc.polyfrost.oneconfig.config.core.ConfigUtils
import cc.polyfrost.oneconfig.config.elements.BasicOption
import cc.polyfrost.oneconfig.gui.elements.BasicButton
import cc.polyfrost.oneconfig.gui.elements.IFocusable
import cc.polyfrost.oneconfig.libs.universal.UKeyboard
import cc.polyfrost.oneconfig.utils.InputHandler
import cc.polyfrost.oneconfig.utils.color.ColorPalette
import cc.polyfrost.oneconfig.utils.dsl.*
import org.polyfrost.polykeystrokes.config.Element
import org.polyfrost.polykeystrokes.config.KeyElement
import org.polyfrost.polykeystrokes.config.ModConfig
import org.polyfrost.polykeystrokes.util.MouseUtils.isFirstClicked
import org.polyfrost.polykeystrokes.config.ModConfig.elements

private const val BORDER_COLOR = 0xFFFFFFFF.toInt()

private fun Element.drawEditing(vg: Long) = nanoVG(vg) {
    draw()

    drawHollowRoundedRect(
        x = position.x - 0.25f,
        y = position.y - 0.25f,
        width = position.width + 0.25f,
        height = position.height + 0.25f,
        radius = 0,
        color = BORDER_COLOR,
        thickness = 0.5f
    )
}

class LayoutEditor(
    category: String,
    subcategory: String,
) : BasicOption(null, null, "layout", "", category, subcategory, 1), IFocusable {
    private val addButton = BasicButton(232, 32, "Add Key", BasicButton.ALIGNMENT_CENTER, ColorPalette.PRIMARY)
    private val deleteButton = BasicButton(232, 32, "Delete", BasicButton.ALIGNMENT_CENTER, ColorPalette.PRIMARY_DESTRUCTIVE)
    private var elementSettings: List<BasicOption>? = null
    private var selection: Selection? = null
    private var draggingState: DraggingState? = null
    private var lastSelected: Element? = null

    init {
        addButton.setClickAction {
            val key = KeyElement()
            ModConfig.keystrokes.keys.add(key)
            selection = Selection(key)
        }
        deleteButton.setClickAction {
            selection?.run {
                ModConfig.keystrokes.keys.removeAll(selectedElements)
                selection = null
            }
        }
    }

    override fun getHeight() = 384
    override fun hasFocus() = true

    override fun draw(vg: Long, x: Int, y: Int, inputHandler: InputHandler) {
        vg.drawHollowRoundedRect(
            x = x + 496,
            y = y,
            width = 480,
            height = 480,
            radius = 10,
            color = 0xFF555555.toInt(),
            thickness = 1
        )

        vg.translate(x.toFloat() + 496f, y.toFloat())
        vg.scale(2f, 2f)


        for (key in elements) {
            key.drawEditing(vg)
        }

        val mouseX = (inputHandler.mouseX().toInt() - x - 496) / 2
        val mouseY = (inputHandler.mouseY().toInt() - y) / 2

        with(inputHandler) {
            if (isAreaHovered(x + 496f, y.toFloat(), 512f, 480f)) when {
                isFirstClicked -> onClicked(mouseX, mouseY)
                isMouseDown -> onDragged(mouseX, mouseY)
                isClicked -> onReleased()
            } else onReleased()
        }

        selection?.draw(vg)
        draggingState?.draw(vg, mouseX, mouseY)

        vg.scale(0.5f, 0.5f)
        vg.translate(-x.toFloat() - 496f, -y.toFloat())

        selection?.selectedElements?.takeIf {
            it.size == 1
        }?.first()?.takeIf {
            it != lastSelected
        }?.let {
            lastSelected = it
            elementSettings = ConfigUtils.getClassOptions(lastSelected)
        }

        var yOption = y + 64
        elementSettings?.forEach { option ->
            option.draw(vg, x, yOption, inputHandler)
            yOption += option.height + 16
        }

        selection?.let {
            deleteButton.draw(vg, x + 240f, y.toFloat(), inputHandler)
            TopButtons.draw(it.selectedElements, vg, x, y, inputHandler)
        }
        addButton.draw(vg, x.toFloat(), y.toFloat(), inputHandler)
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
        elements.lastOrNull { element ->
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

    override fun keyTyped(char: Char, keyCode: Int) {
        super.keyTyped(char, keyCode)

        val selection = selection ?: return
        if (elementSettings?.any { option ->
                option.keyTyped(char, keyCode)
                (option as? IFocusable)?.hasFocus() == true
            } == true) return

        val selectionPos = selection.position
        when (keyCode) {
            UKeyboard.KEY_UP -> selectionPos.y--
            UKeyboard.KEY_DOWN -> selectionPos.y++
            UKeyboard.KEY_LEFT -> selectionPos.x--
            UKeyboard.KEY_RIGHT -> selectionPos.x++
        }
    }

} // todo shift disable snap ctrl square remove multi-resize