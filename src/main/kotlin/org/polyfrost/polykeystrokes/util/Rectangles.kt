package org.polyfrost.polykeystrokes.util

interface Rectangle {
    val x: Int
    val y: Int
    val width: Int
    val height: Int
    val xCenter: Int get() = x + width / 2
    val xCenterFloat: Float get() = x + width / 2f
    val xRight: Int get() = x + width
    val yCenter: Int get() = y + height / 2
    val yCenterFloat: Float get() = y + height / 2f
    val yBottom: Int get() = y + height

    infix fun intersects(rectangle: Rectangle): Boolean =
        x < rectangle.xRight && xRight > rectangle.x && y < rectangle.yBottom && yBottom > rectangle.y

    fun contains(pointX: Int, pointY: Int): Boolean = pointX in x..xRight && pointY in y..yBottom
}

interface MutableRectangle : Rectangle {
    override var x: Int
    override var y: Int
    override var width: Int
    override var height: Int

    override var xCenter: Int
        get() = super.xCenter
        set(value) {
            x = value - width / 2
        }

    override var xRight: Int
        get() = super.xRight
        set(value) {
            x = value - width
        }

    override var yCenter: Int
        get() = super.yCenter
        set(value) {
            y = value - height / 2
        }

    override var yBottom: Int
        get() = super.yBottom
        set(value) {
            y = value - height
        }
}

class IntRectangle(
    override var x: Int,
    override var y: Int,
    width: Int,
    height: Int,
) : MutableRectangle {
    override var width: Int = width
        set(value) {
            field = value.coerceAtLeast(16)
        }

    override var height: Int = height
        set(value) {
            field = value.coerceAtLeast(16)
        }
}

open class UnionRectangle(private val rectangles: List<Rectangle>) : Rectangle {
    override val x: Int get() = rectangles.minOfOrNull { it.x } ?: 0
    override val y: Int get() = rectangles.minOfOrNull { it.y } ?: 0
    override val width: Int get() = (rectangles.maxOfOrNull { it.xRight } ?: 0) - x
    override val height: Int get() = (rectangles.maxOfOrNull { it.yBottom } ?: 0) - y
}

class MutableUnionRectangle(
    rectangles: List<MutableRectangle>,
) : UnionRectangle(rectangles), MutableRectangle {
    private val scaledRectangles: List<ScaledRectangle> = rectangles.map {
        ScaledRectangle(original = it, scaleTo = this)
    }

    override var x: Int
        get() = super.x
        set(value) = scaledRectangles.forEach { it.x = value }

    override var y: Int
        get() = super.y
        set(value) = scaledRectangles.forEach { it.y = value }

    override var width: Int
        get() = super.width
        set(value) {
            val coerced = value.coerceAtLeast(widthLimit)
            scaledRectangles.forEach { it.width = coerced }
            x = x
        }

    override var height: Int
        get() = super.height
        set(value) {
            val coerced = value.coerceAtLeast(heightLimit)
            scaledRectangles.forEach { it.height = coerced }
            y = y
        }

    private val widthLimit: Int
        get() = scaledRectangles.maxOfOrNull { it.widthLimit } ?: 0

    private val heightLimit: Int
        get() = scaledRectangles.maxOfOrNull { it.heightLimit } ?: 0
}

private class ScaledRectangle(
    val original: MutableRectangle,
    val xOffsetScale: Float,
    val yOffsetScale: Float,
    val widthScale: Float,
    val heightScale: Float,
) : MutableRectangle {
    constructor(original: MutableRectangle, scaleTo: MutableRectangle) : this(
        original = original,
        xOffsetScale = (scaleTo.x - original.x) / original.width.toFloat(),
        yOffsetScale = (scaleTo.y - original.y) / original.height.toFloat(),
        widthScale = original.width / scaleTo.width.toFloat(),
        heightScale = original.height / scaleTo.height.toFloat()
    )

    override var x: Int
        get() = original.x + xOffset
        set(value) {
            original.x = value - xOffset
        }

    private val xOffset: Int
        get() = (xOffsetScale * original.width).toInt()

    override var y: Int
        get() = original.y + yOffset
        set(value) {
            original.y = value - yOffset
        }

    private val yOffset: Int
        get() = (yOffsetScale * original.height).toInt()


    override var width: Int
        get() = (original.width / widthScale).toInt()
        set(value) {
            original.width = (value * widthScale).toInt()
        }

    override var height: Int
        get() = (original.height / heightScale).toInt()
        set(value) {
            original.height = (value * heightScale).toInt()
        }

    val widthLimit get() = (16 / widthScale).toInt()
    val heightLimit get() = (16 / heightScale).toInt()
}

