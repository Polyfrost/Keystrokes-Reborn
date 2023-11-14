@file:Suppress("UnstableApiUsage")

package org.polyfrost.polykeystrokes.gui

import cc.polyfrost.oneconfig.config.core.ConfigUtils
import cc.polyfrost.oneconfig.config.elements.BasicOption
import cc.polyfrost.oneconfig.gui.elements.BasicButton
import cc.polyfrost.oneconfig.gui.elements.IFocusable
import cc.polyfrost.oneconfig.libs.universal.UKeyboard
import cc.polyfrost.oneconfig.renderer.font.Fonts
import cc.polyfrost.oneconfig.utils.InputHandler
import cc.polyfrost.oneconfig.utils.color.ColorPalette
import cc.polyfrost.oneconfig.utils.dsl.*
import cc.polyfrost.oneconfig.utils.gui.GuiUtils
import org.polyfrost.polykeystrokes.config.Element
import org.polyfrost.polykeystrokes.config.KeyElement
import org.polyfrost.polykeystrokes.config.ModConfig
import org.polyfrost.polykeystrokes.config.ModConfig.elements

class LayoutEditor(
    category: String,
    subcategory: String,
) : BasicOption(null, null, "Layout", "", category, subcategory, 2), IFocusable {
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

    override fun getHeight() = 240
    override fun hasFocus() = true

    override fun draw(vg: Long, x: Int, y: Int, inputHandler: InputHandler) {
        vg.drawRoundedRect(
            x = x + 496,
            y = y,
            width = 496,
            height = 384,
            radius = 10,
            color = 0xFF313338.toInt()
        )

        vg.translate(x.toFloat() + 496f, y.toFloat())
        vg.scale(2f, 2f)

        nanoVG(vg) {
            for (key in elements) key.run {
                draw()
            }
        }
        val mouseX = (inputHandler.mouseX().toInt() - x - 496) / 2
        val mouseY = (inputHandler.mouseY().toInt() - y) / 2

        with(inputHandler) {
            if (isAreaHovered(x + 496f, y.toFloat(), 496f, 384f)) when {
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

        vg.drawText(name, x.toFloat(), y + 17f, nameColor, 24f, Fonts.MEDIUM)
        addButton.draw(vg, x.toFloat(), y + 48f, inputHandler)
        selection?.let {
            deleteButton.draw(vg, x + 240f, y + 48f, inputHandler)
        }
        var yOption = y + 96
        elementSettings?.forEach { option ->
            option.draw(vg, x, yOption, inputHandler)
            yOption += option.height + 16
        }

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

private val InputHandler.isFirstClicked get() = !GuiUtils.wasMouseDown() && isMouseDown
