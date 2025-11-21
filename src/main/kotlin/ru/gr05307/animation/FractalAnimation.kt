package ru.gr05307.animation

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import ru.gr05307.ui.PaintPanel
import ru.gr05307.ui.SelectionPanel
import kotlinx.coroutines.Dispatchers
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
    private val width = viewModel.fractalImage.width
    private val height = viewModel.fractalImage.height

    private fun createFrames(nFrames: Int = 30) {
        for (n in 1..nFrames){
            val d = n.toDouble() / nFrames
            val tempFramePlain = Plain(
                xMin = (lastFramePlain.xMin - firstFramePlain.xMin) * d + firstFramePlain.xMin,
                xMax = (lastFramePlain.xMax - firstFramePlain.xMax) * d + firstFramePlain.xMax,
                yMin = (lastFramePlain.yMin - firstFramePlain.yMin) * d + firstFramePlain.yMin,
                yMax = (lastFramePlain.yMax - firstFramePlain.yMax) * d + firstFramePlain.yMax
            )
        }
    }

    private suspend fun startAnimation() = withContext(Dispatchers.Default) {
    }

}