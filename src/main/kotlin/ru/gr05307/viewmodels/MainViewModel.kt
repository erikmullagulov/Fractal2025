package ru.gr05307.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import ru.gr05307.animation.FractalAnimation
import ru.gr05307.painting.FractalPainter
import ru.gr05307.painting.convertation.Converter
import ru.gr05307.painting.convertation.Plain

class MainViewModel{
    var fractalImage by mutableStateOf(ImageBitmap(0, 0))
    var selectionOffset by mutableStateOf(Offset(0f, 0f))
    var selectionSize by mutableStateOf(Size(0f, 0f))
    private val plain = Plain(-2.0,1.0,-1.0,1.0)
    private val fractalPainter = FractalPainter(plain)
    private var mustRepaint by mutableStateOf(true)

    fun paint(scope: DrawScope) = runBlocking {
        plain.width = scope.size.width
        plain.height = scope.size.height
        if (mustRepaint
            || fractalImage.width != plain.width.toInt()
            || fractalImage.height != plain.height.toInt()
        ) {
            launch (Dispatchers.Default) {
                fractalPainter.paint(scope)
            }
        }
        else
            scope.drawImage(fractalImage)
        mustRepaint = false
    }

    fun onImageUpdate(image: ImageBitmap) {
        fractalImage = image
    }

    fun onStartSelecting(offset: Offset){
        this.selectionOffset = offset
    }

    fun onStopSelecting(){
        val firstFramePlain = plain.copy()
        val xMin = Converter.xScr2Crt(selectionOffset.x, plain)
        val yMin = Converter.yScr2Crt(selectionOffset.y+selectionSize.height, plain)
        val xMax = Converter.xScr2Crt(selectionOffset.x+selectionSize.width, plain)
        val yMax = Converter.yScr2Crt(selectionOffset.y, plain)
        plain.xMin = xMin
        plain.yMin = yMin
        plain.xMax = xMax
        plain.yMax = yMax
        selectionSize = Size(0f,0f)
        val animation = FractalAnimation(
            firstFramePlain = firstFramePlain,
            lastFramePlain = plain,
            frameOut = { frame -> fractalImage = frame },
            animationOn = true
        )
        CoroutineScope(Dispatchers.Default).launch {
            animation.startAnimation()
            fractalPainter.plain = plain
            mustRepaint = true
        }
    }

    fun onSelecting(offset: Offset){
        selectionSize = Size(selectionSize.width + offset.x, selectionSize.height + offset.y)
    }
}