package ru.gr05307.painting.convertation

data class Plain(
    var xMin: Double,
    var xMax: Double,
    var yMin: Double,
    var yMax: Double,
    var width: Float = 0f,
    var height: Float = 0f,
){
    val xDen get() = width / (xMax - xMin)
    val yDen get() = height / (yMax - yMin)

    val aspectRatio: Double
        get() = if (yMax - yMin != 0.0) (xMax - xMin) / (yMax - yMin) else 1.0
}
