@file:Suppress("UnstableApiUsage")

package org.polyfrost.polykeystrokes.gui

import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.elements.BasicOption
import cc.polyfrost.oneconfig.gui.OneConfigGui
import cc.polyfrost.oneconfig.gui.elements.BasicButton
import cc.polyfrost.oneconfig.gui.elements.text.TextInputField
import cc.polyfrost.oneconfig.libs.universal.UKeyboard
import cc.polyfrost.oneconfig.utils.InputHandler
import cc.polyfrost.oneconfig.utils.color.ColorPalette
import org.polyfrost.polykeystrokes.config.Element
import org.polyfrost.polykeystrokes.config.KeyElement
import org.polyfrost.polykeystrokes.config.MouseElement

object TopButtons : BasicOption(null, null, "top buttons", "", "General", "", 2) {
    private var addButton = BasicButton(128, 32, "Add Key", BasicButton.ALIGNMENT_CENTER, ColorPalette.PRIMARY)
    private var deleteButton = BasicButton(128, 32, "Delete", BasicButton.ALIGNMENT_CENTER, ColorPalette.PRIMARY_DESTRUCTIVE)
    var elements: List<Element> = emptyList()

    override fun draw(vg: Long, x: Int, y: Int, inputHandler: InputHandler) {
        addButton.draw(vg, x.toFloat(), y.toFloat(), inputHandler)
        if (elements.isEmpty()) return
        deleteButton.draw(vg, x.toFloat() + 160, y.toFloat(), inputHandler)
        if (elements.size != 1) return
        when (val element = elements[0]) {
            is KeyElement -> {
                KeyTextField.drawWithKeyBind(element, vg, x + 320, y, inputHandler)
                KeyBindButton.drawWithKeyBind(element.keybind, vg, x + 608, y, inputHandler)
            }

            is MouseElement -> {}
            else -> {}
        }
    }

    override fun getHeight() = 32

    override fun keyTyped(char: Char, keyCode: Int) {
        super.keyTyped(char, keyCode)
        if (elements.size != 1) return
        when (val element = elements[0]) {
            is KeyElement -> {
                KeyBindButton.isKeyTyped(element.keybind, keyCode)
                KeyTextField.isKeyTyped(element, char, keyCode)
            }
        }
    }
}

object KeyTextField {
    private val textField = TextInputField(256, 32, "", false, false)
    private var currentKey: KeyElement? = null
        set(value) {
            value ?: return
            if (value == field) return
            field = value
            textField.input = value.text
            textField.isToggled = false
        }

    fun drawWithKeyBind(key: KeyElement, vg: Long, x: Int, y: Int, inputHandler: InputHandler) {
        currentKey = key
        textField.draw(vg, x.toFloat(), y.toFloat(), inputHandler)
    }

    fun isKeyTyped(key: KeyElement, char: Char, keyCode: Int) {
        currentKey = key
        textField.keyTyped(char, keyCode)
        key.text = textField.input
    }
}

private object KeyBindButton {
    private val keyBindButton: BasicButton = BasicButton(256, 32, "", BasicButton.ALIGNMENT_JUSTIFIED, ColorPalette.SECONDARY)
    private var currentKeyBind: OneKeyBind? = null
        set(value) {
            if (value == field) return
            field = value
            stopRecording()
        }

    private fun stopRecording() {
        keyBindButton.isToggled = false
        OneConfigGui.INSTANCE.allowClose = true
    }

    init {
        keyBindButton.setToggleable(true)
        keyBindButton.setClickAction {
            OneConfigGui.INSTANCE.allowClose = !keyBindButton.isToggled
        }
    }

    fun drawWithKeyBind(keyBind: OneKeyBind, vg: Long, x: Int, y: Int, inputHandler: InputHandler) {
        currentKeyBind = keyBind

        keyBindButton.draw(vg, x.toFloat(), y.toFloat(), inputHandler)

        if (keyBindButton.isToggled) whileListening(keyBind)
        keyBindButton.text = getDisplayText(keyBind)
    }

    private fun whileListening(keyBind: OneKeyBind) {
        when {
            keyBindButton.isClicked -> keyBind.clearKeys()
            keyBind.size != 0 && !keyBind.isActive -> stopRecording()
        }
    }

    private fun getDisplayText(keyBind: OneKeyBind) = keyBind.display.ifEmpty {
        if (keyBindButton.isToggled) "Recording... (ESC to clear)"
        else "None"
    }

    fun isKeyTyped(keyBind: OneKeyBind, keyCode: Int) {
        currentKeyBind = keyBind

        if (!keyBindButton.isToggled) return

        when (keyCode) {
            UKeyboard.KEY_ESCAPE -> {
                keyBind.clearKeys()
                stopRecording()
            }

            else -> keyBind.addKey(keyCode)
        }
    }
}