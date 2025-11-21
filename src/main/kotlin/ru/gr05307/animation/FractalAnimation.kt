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
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import ru.gr05307.painting.FractalPainter
import ru.gr05307.viewmodels.MainViewModel
import ru.gr05307.painting.convertation.Plain

class FractalAnimation(
    private val firstFramePlain: Plain,
    private val lastFramePlain: Plain,
    private val viewModel: MainViewModel,
    private val fractalPainter: FractalPainter,
    private val animationOn: Boolean
) {
    private val fractalFrames = mutableListOf<ImageBitmap>()

    private suspend fun createFrames(nFrames: Int = 30) = withContext(Dispatchers.Default) {
        for (n in 1..nFrames){
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
            fractalFrames += plainToImage(tempFramePlain)
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

    private suspend fun startAnimation() = withContext(Dispatchers.Default) {

        }

}