package org.polyfrost.polykeystrokes.util

interface Rectangle {
    var x: Int
    var y: Int
    var width: Int
    var height: Int

    var xCenter
        get() = x + width / 2
        set(value) {
            x = value - width / 2
        }

    var xRight
        get() = x + width
        set(value) {
            x = value - width
        }

    var yCenter
        get() = y + height / 2
        set(value) {
            y = value - height / 2
        }

    var yBottom
        get() = y + height
        set(value) {
            y = value - height
        }

    infix fun intersects(rectangle: Rectangle) =
        x < rectangle.xRight && xRight > rectangle.x && y < rectangle.yBottom && yBottom > rectangle.y

    fun contains(pointX: Int, pointY: Int) = pointX in x..xRight && pointY in y..yBottom

    fun offsetBy(xOffset: Int, yOffset: Int, widthOffset: Int = 0, heightOffset: Int = 0) =
        OffsettedRectangle(this, xOffset, yOffset, widthOffset, heightOffset)

    fun offsetTo(xPoint: Int, yPoint: Int) = offsetBy(xPoint - x, yPoint - y)

    fun offsetTo(rectangle: Rectangle) = offsetBy(rectangle.x - x, rectangle.y - y, rectangle.width - width, rectangle.height - height)
}

class IntRectangle(
    override var x: Int,
    override var y: Int,
    width: Int,
    height: Int,
) : Rectangle {
    override var width: Int = width
        set(value) {
            field = value.coerceAtLeast(10)
        }

    override var height: Int = height
        set(value) {
            field = value.coerceAtLeast(10)
        }
}

data class OffsettedRectangle(
    val original: Rectangle,
    var xOffset: Int,
    var yOffset: Int,
    var widthOffset: Int = 0,
    var heightOffset: Int = 0,
) : Rectangle {
    override var x
        get() = original.x + xOffset
        set(value) {
            original.x = value - xOffset
        }

    override var y
        get() = original.y + yOffset
        set(value) {
            original.y = value - yOffset
        }

    override var width
        get() = original.width + widthOffset
        set(value) {
            original.width = value - widthOffset
        }

    override var height
        get() = original.height + heightOffset
        set(value) {
            original.height = value - heightOffset
        }
}