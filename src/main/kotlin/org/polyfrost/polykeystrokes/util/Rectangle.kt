package org.polyfrost.polykeystrokes.util

interface Rectangle {
    var x: Int
    var y: Int
    var width: Int
    var height: Int

    var xCenter: Int
        get() = x + width / 2
        set(value) {
            x = value - width / 2
        }

    var xRight: Int
        get() = x + width
        set(value) {
            x = value - width
        }

    var yCenter: Int
        get() = y + height / 2
        set(value) {
            y = value - height / 2
        }

    var yBottom: Int
        get() = y + height
        set(value) {
            y = value - height
        }

    infix fun intersects(rectangle: Rectangle): Boolean =
        x < rectangle.xRight && xRight > rectangle.x && y < rectangle.yBottom && yBottom > rectangle.y

    fun contains(pointX: Int, pointY: Int): Boolean = pointX in x..xRight && pointY in y..yBottom

    fun offsetBy(xOffset: Int, yOffset: Int, widthOffset: Int = 0, heightOffset: Int = 0) =
        OffsettedRectangle(this, xOffset, yOffset, widthOffset, heightOffset)

    fun offsetTo(xPoint: Int, yPoint: Int) = offsetBy(xPoint - x, yPoint - y)

    fun offsetTo(rectangle: Rectangle) = offsetBy(rectangle.x - x, rectangle.y - y, rectangle.width - width, rectangle.height - height)

    operator fun plus(rectangle: Rectangle) = UnionRectangle(listOf(this, rectangle))
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
    override var x: Int
        get() = original.x + xOffset
        set(value) {
            original.x = value - xOffset
        }

    override var y: Int
        get() = original.y + yOffset
        set(value) {
            original.y = value - yOffset
        }

    override var width: Int
        get() = original.width + widthOffset
        set(value) {
            original.width = value - widthOffset
        }

    override var height: Int
        get() = original.height + heightOffset
        set(value) {
            original.height = value - heightOffset
        }
}

data class UnionRectangle(
    val rects: List<Rectangle>,
) : Rectangle {
    private val scaledRectangles: List<Rectangle> by lazy {
        rects.map {
            ScaledRectangle(
                original = it,
                xOffsetScale = (x - it.x) / it.width.toFloat(),
                yOffsetScale = (y - it.y) / it.height.toFloat(),
                widthScale = it.width / width.toFloat(),
                heightScale = it.height / height.toFloat()
            )
        }
    }

    override var x: Int
        get() = rects.minOfOrNull { it.x } ?: 0
        set(value) = scaledRectangles.forEach { it.x = value }

    override var y: Int
        get() = rects.minOfOrNull { it.y } ?: 0
        set(value) = scaledRectangles.forEach { it.y = value }

    override var width: Int
        get() = (rects.maxOfOrNull { it.xRight } ?: 0) - x
        set(value) {
            scaledRectangles.forEach { it.width = value }
            x = x
        }

    override var height: Int
        get() = (rects.maxOfOrNull { it.yBottom } ?: 0) - y
        set(value) {
            scaledRectangles.forEach { it.height = value }
            y = y
        }

    override fun plus(rectangle: Rectangle) = UnionRectangle(rects + rectangle)
}

data class ScaledRectangle(
    val original: Rectangle,
    var xOffsetScale: Float,
    var yOffsetScale: Float,
    var widthScale: Float,
    var heightScale: Float,
) : Rectangle {
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
}

