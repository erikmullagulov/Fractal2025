package ru.gr05307.animation

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import ru.gr05307.ui.PaintPanel
import ru.gr05307.ui.SelectionPanel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import ru.gr05307.painting.FractalPainter
import ru.gr05307.viewmodels.MainViewModel
import ru.gr05307.painting.convertation.Plain

class FractalAnimation(
    private val firstFramePlain: Plain,
    private val lastFramePlain: Plain,
    private val frameOut: (ImageBitmap) -> Unit = {},
    private val fractalPainter: FractalPainter,
    val animationOn: Boolean
) {

    suspend fun startAnimation(nFrames: Int = 60) = coroutineScope {
        if (!animationOn) return@coroutineScope

        val jobs = (1..nFrames).map { n ->
            async(Dispatchers.Default) {
                val d = n.toDouble() / nFrames
                val tempFramePlain = Plain(
                    xMin = (lastFramePlain.xMin - firstFramePlain.xMin) * d + firstFramePlain.xMin,
                    xMax = (lastFramePlain.xMax - firstFramePlain.xMax) * d + firstFramePlain.xMax,
                    yMin = (lastFramePlain.yMin - firstFramePlain.yMin) * d + firstFramePlain.yMin,
                    yMax = (lastFramePlain.yMax - firstFramePlain.yMax) * d + firstFramePlain.yMax,
                    width = firstFramePlain.width,
                    height = firstFramePlain.height
                )
                fractalPainter.plain = tempFramePlain
                n to plainToImage(tempFramePlain)
            }
        }
        val frames = jobs.awaitAll().sortedBy { it.first }.map { it.second }
        for (frame in frames) {
            frameOut(frame)
            delay(30)
        }
    }

    private suspend fun plainToImage(plain: Plain): ImageBitmap {
        val image = ImageBitmap(plain.width.toInt(), plain.height.toInt())
        val canvas = Canvas(image)
        val drawScope = CanvasDrawScope()
        drawScope.draw(
            Density(1f),
            LayoutDirection.Ltr,
            canvas,
            Size(plain.width, plain.height)
        ) {
            fractalPainter.paint(this)
        }
        return image
    }

}