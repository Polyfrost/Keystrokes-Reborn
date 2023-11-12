@file:Suppress("UnstableApiUsage")

package org.polyfrost.polykeystrokes.gui

import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.events.EventManager
import cc.polyfrost.oneconfig.events.event.RawMouseEvent
import cc.polyfrost.oneconfig.gui.OneConfigGui
import cc.polyfrost.oneconfig.gui.elements.BasicButton
import cc.polyfrost.oneconfig.gui.elements.text.TextInputField
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe
import cc.polyfrost.oneconfig.libs.universal.UKeyboard
import cc.polyfrost.oneconfig.utils.InputHandler
import cc.polyfrost.oneconfig.utils.color.ColorPalette
import org.polyfrost.polykeystrokes.config.Element
import org.polyfrost.polykeystrokes.config.KeyElement
import org.polyfrost.polykeystrokes.config.MouseElement

object TopButtons {
    fun draw(elements: Collection<Element>, vg: Long, x: Int, y: Int, inputHandler: InputHandler) {
        if (elements.size != 1) return
        when (val element = elements.first()) {
            is KeyElement -> {
                KeyTextField.drawWithKeyBind(element, vg, x, y + 96, inputHandler)
                KeyBindButton.drawWithKeyBind(element.keybind, vg, x, y + 144, inputHandler)
            }

            is MouseElement -> {}
            else -> {}
        }
    }

    fun keyTyped(elements: Collection<Element>, char: Char, keyCode: Int): Boolean {
        if (elements.size != 1) return false
        when (val element = elements.first()) {
            is KeyElement ->
                return KeyTextField.isKeyTyped(element, char, keyCode)
                    || KeyBindButton.isKeyTyped(element.keybind, keyCode)
        }
        return false
    }
}

object KeyTextField {
    private val textField = TextInputField(128, 32, "", false, false)
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

    fun isKeyTyped(key: KeyElement, char: Char, keyCode: Int): Boolean {
        if (!textField.isToggled) return false
        currentKey = key
        textField.keyTyped(char, keyCode)
        key.text = textField.input
        return true
    }
}

private object KeyBindButton {
    private val keyBindButton: BasicButton = BasicButton(128, 32, "", BasicButton.ALIGNMENT_JUSTIFIED, ColorPalette.SECONDARY)
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

    @Subscribe
    private fun onMouse(event: RawMouseEvent) {
        if (keyBindButton.isToggled && event.state == 1) {
            currentKeyBind?.addKey(event.button, true)
        }
    }

    init {
        EventManager.INSTANCE.register(this)
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

    fun isKeyTyped(keyBind: OneKeyBind, keyCode: Int): Boolean {
        currentKeyBind = keyBind

        if (!keyBindButton.isToggled) return false

        when (keyCode) {
            UKeyboard.KEY_ESCAPE -> {
                keyBind.clearKeys()
                stopRecording()
            }

            else -> keyBind.addKey(keyCode)
        }
        return true
    }
}