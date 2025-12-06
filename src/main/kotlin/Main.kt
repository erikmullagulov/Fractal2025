import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ru.gr05307.ui.PaintPanel
import ru.gr05307.ui.SelectionPanel
import ru.gr05307.viewmodels.AppViewModel
import ru.gr05307.julia.ResizableJuliaPanel
import ru.gr05307.viewmodels.MainViewModel
import ru.gr05307.math.Complex
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.material.*
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import ru.gr05307.viewmodels.JuliaViewModel


@Composable
@Preview
fun App() {
    val viewModel = remember { AppViewModel() }

    MaterialTheme {
        FractalApp(viewModel)
    }
}

@Composable
fun FractalApp(viewModel: AppViewModel) {
    Row(modifier = Modifier.fillMaxSize()) {
        MainFractalView(
            viewModel = viewModel.mainViewModel,
            modifier = Modifier
                .fillMaxHeight()
                .weight(7f)
        )

        JuliaSidePanel(
            viewModel = viewModel.juliaViewModel,
            modifier = Modifier
                .fillMaxHeight()
                .weight(3f)
        )
    }
}

@Composable
fun MainFractalView(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Панель кнопок сверху
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray.copy(alpha = 0.1f))
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { viewModel.switchToRainbow() },
                modifier = Modifier.height(36.dp)
            ) {
                Text("Rainbow")
            }
            Button(
                onClick = { viewModel.switchToGrayscale() },
                modifier = Modifier.height(36.dp)
            ) {
                Text("Grayscale")
            }
            Button(
                onClick = { viewModel.switchToIce() },
                modifier = Modifier.height(36.dp)
            ) {
                Text("Ice")
            }
            Button(
                onClick = { viewModel.switchToNewtonColor() },
                modifier = Modifier.height(36.dp)
            ) {
                Text("NewtonColor")
            }
            Button(
                onClick = { viewModel.switchToMandelbrot() },
                modifier = Modifier.height(36.dp)
            ) {
                Text("Mandelbrot")
            }
            Button(
                onClick = { viewModel.switchToJulia() },
                modifier = Modifier.height(36.dp)
            ) {
                Text("Julia")
            }
            Button(
                onClick = { viewModel.switchToNewton() },
                modifier = Modifier.height(36.dp)
            ) {
                Text("Newton")
            }
        }

        // Область с фракталом (занимает всё оставшееся пространство)
        Box(modifier = Modifier.weight(1f)) {
            PaintPanel(
                modifier = Modifier.fillMaxSize(),
                onImageUpdate = { image -> viewModel.onImageUpdate(image) },
                onPaint = { scope -> viewModel.paint(scope) }
            )
            SelectionPanel(
                viewModel.selectionOffset,
                viewModel.selectionSize,
                Modifier.fillMaxSize(),
                onClick = { pos -> viewModel.onPointClicked(pos.x, pos.y) },
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

@Composable
fun JuliaSidePanel(
    viewModel: JuliaViewModel,
    modifier: Modifier = Modifier
) {
    val currentJuliaPoint = viewModel.currentJuliaPoint
    val showJuliaPanel = viewModel.showJuliaPanel

    AnimatedVisibility(
        visible = showJuliaPanel && currentJuliaPoint != null,
        enter = slideInHorizontally(animationSpec = tween(300)) { it },
        exit = slideOutHorizontally(animationSpec = tween(300)) { it },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .background(Color.White)
                .border(1.dp, Color.Gray)
        ) {
            PanelHeader(
                onClose = { viewModel.closeJuliaPanel() }
            )

            if (currentJuliaPoint != null) {
                PointInfoCard(currentJuliaPoint)
            }

            if (currentJuliaPoint != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    ResizableJuliaPanel(
                        c = currentJuliaPoint,
                        onClose = { viewModel.closeJuliaPanel() },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun PanelHeader(
    onClose: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.primary)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Множество Жюлиа",
            color = Color.White,
            style = MaterialTheme.typography.h6
        )
        IconButton(
            onClick = onClose
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Закрыть",
                tint = Color.White
            )
        }
    }
}

@Composable
fun PointInfoCard(c: Complex) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Выбранная точка:",
                style = MaterialTheme.typography.subtitle1
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "c = ${"%.6f".format(c.re)} + ${"%.6f".format(c.im)}i",
                style = MaterialTheme.typography.body1
            )
        }
    }
}

fun main(): Unit = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Фрактал - 2025 (гр. 05-307)"
    ) {
        App()
    }
}