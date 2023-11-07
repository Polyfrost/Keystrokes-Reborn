package org.polyfrost.polykeystrokes.util


data class IntRectangle(
    var x: Int,
    var y: Int,
    var width: Int,
    var height: Int,
) {
    val xCenter get() = x + width / 2
    val xRight get() = x + width
    val yCenter get() = y + height / 2
    val yBottom get() = y + height

    infix fun intersects(rect: IntRectangle) =
        x < rect.xRight && xRight > rect.x && y < rect.yBottom && yBottom > rect.y

    fun contains(pointX: Int, pointY: Int) = pointX in x..xRight && pointY in y..yBottom
}
