import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ru.gr05307.ui.PaintPanel
import ru.gr05307.ui.SelectionPanel
import ru.gr05307.viewmodels.MainViewModel
import androidx.compose.runtime.*
// Добавления от Артёма
import ru.gr05307.julia.JuliaWindow
import ru.gr05307.math.Complex

class JuliaViewModelWrapper(
    private val baseViewModel: MainViewModel,
    private val onJuliaPointSelected: (Complex) -> Unit
) {
    // Делегируем все методы базовому ViewModel
    val fractalImage get() = baseViewModel.fractalImage
    val selectionOffset get() = baseViewModel.selectionOffset
    val selectionSize get() = baseViewModel.selectionSize

    fun paint(scope: androidx.compose.ui.graphics.drawscope.DrawScope) = baseViewModel.paint(scope)
    fun onImageUpdate(image: androidx.compose.ui.graphics.ImageBitmap) = baseViewModel.onImageUpdate(image)
    fun onStartSelecting(offset: androidx.compose.ui.geometry.Offset) = baseViewModel.onStartSelecting(offset)
    fun onSelecting(offset: androidx.compose.ui.geometry.Offset) = baseViewModel.onSelecting(offset)
    fun onStopSelecting() = baseViewModel.onStopSelecting()
    fun canUndo() = baseViewModel.canUndo()
    fun performUndo() = baseViewModel.performUndo()
    fun onPanning(offset: androidx.compose.ui.geometry.Offset) = baseViewModel.onPanning(offset)
    fun saveFractalToJpg(path: String) = baseViewModel.saveFractalToJpg(path)

    // Переопределяем обработку кликов
    fun onPointClicked(x: Float, y: Float) {
        val re = ru.gr05307.painting.convertation.Converter.xScr2Crt(x, baseViewModel.plain)
        val im = ru.gr05307.painting.convertation.Converter.yScr2Crt(y, baseViewModel.plain)
        onJuliaPointSelected(Complex(re, im))
    }
}

@Composable
@Preview
fun App(viewModel: MainViewModel = MainViewModel()) {
    MaterialTheme {
        Box {
            PaintPanel(
                Modifier.fillMaxSize(),
                onImageUpdate = { image -> viewModel.onImageUpdate(image) },
                onPaint = { scope -> viewModel.paint(scope) },
            )
            SelectionPanel(
                viewModel.selectionOffset,
                viewModel.selectionSize,
                Modifier.fillMaxSize(),
                onDragStart = viewModel::onStartSelecting,
                onDragEnd = viewModel::onStopSelecting,
                onDrag = viewModel::onSelecting,
                onPan = viewModel::onPanning,
            )
            Button(
                onClick = { viewModel.performUndo() },
                enabled = viewModel.canUndo(),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Text("Назад")
            }
        }
    }
}

fun main(): Unit = application {

    var juliaPoints by remember { mutableStateOf<List<Complex>>(emptyList()) }

    // Главное окно
    Window(
        onCloseRequest = ::exitApplication,
        title = "Фрактал - 2025 (гр. 05-307)"
    ) {
        // Создаем обычный ViewModel
        val baseViewModel = remember { MainViewModel() }

        // Создаем обертку для обработки кликов
        val wrappedViewModel = remember {
            JuliaViewModelWrapper(baseViewModel) { complex ->
                juliaPoints = juliaPoints + complex
            }
        }

        // Используем обертку в приложении
        MaterialTheme {
            Box {
                PaintPanel(
                    Modifier.fillMaxSize(),
                    onImageUpdate = { image -> wrappedViewModel.onImageUpdate(image) },
                    onPaint = { scope -> wrappedViewModel.paint(scope) }
                )
                SelectionPanel(
                    wrappedViewModel.selectionOffset,
                    wrappedViewModel.selectionSize,
                    Modifier.fillMaxSize(),
                    onClick = { pos ->
                        println("CLICK AT $pos")
                        wrappedViewModel.onPointClicked(pos.x, pos.y)
                    },
                    onDragStart = wrappedViewModel::onStartSelecting,
                    onDragEnd = wrappedViewModel::onStopSelecting,
                    onDrag = wrappedViewModel::onSelecting,
                    onPan = wrappedViewModel::onPanning,
                )
                Button(
                    onClick = { wrappedViewModel.performUndo() },
                    enabled = wrappedViewModel.canUndo(),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                ) {
                    Text("Назад")
                }
            }
        }
    }

    // Создаем окна Жюлиа для каждой точки
    for ((index, c) in juliaPoints.withIndex()) {
        key(index) {
            JuliaWindow(
                c = c,
                onClose = {
                    juliaPoints = juliaPoints.filterIndexed { i, _ -> i != index }
                }
            )
        }
    }
}


