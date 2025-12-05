package ru.gr05307.julia

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.gr05307.math.Complex

@Composable
fun JuliaPanel(
    c: Complex?,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Если нет точки, не показываем панель
    if (c == null) return

    var imageState by remember { mutableStateOf<List<List<Color>>?>(null) }
    var panelSize by remember { mutableStateOf(IntSize.Zero) }
    var isRendering by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Обработка изменения размера
    LaunchedEffect(c, panelSize) {
        // Проверяем, что размер больше нуля и не происходит рендеринг
        if (panelSize.width > 0 && panelSize.height > 0 && !isRendering) {
            isRendering = true
            scope.launch(Dispatchers.Default) {
                try {
                    imageState = renderJulia(c, panelSize.width, panelSize.height)
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    isRendering = false
                }
            }
        }
    }

    Box(
        modifier = modifier
            .size(320.dp, 240.dp) // Фиксированный размер
            .border(2.dp, Color.Gray)
            .background(Color.White)
    ) {
        // Заголовок с кнопкой закрытия
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Жюлиа: c = ${"%.3f".format(c.re)} + ${"%.3f".format(c.im)}i",
                style = MaterialTheme.typography.caption
            )
            IconButton(
                onClick = onClose,
                modifier = Modifier.size(16.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Закрыть",
                    tint = Color.Red
                )
            }
        }

        // Область с изображением Жюлиа
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 30.dp) // Отступ для заголовка
        ) {
            val newSize = IntSize(size.width.toInt(), size.height.toInt())
            if (newSize.width > 0 && newSize.height > 0 && newSize != panelSize) {
                panelSize = newSize
            }

            val img = imageState ?: return@Canvas

            // Проверяем, что размеры изображения соответствуют размерам Canvas
            val w = minOf(size.width.toInt(), img.size)
            val h = if (w > 0) minOf(size.height.toInt(), img[0].size) else 0

            if (w > 0 && h > 0) {
                for (x in 0 until w) {
                    for (y in 0 until h) {
                        drawRect(
                            color = img[x][y],
                            topLeft = androidx.compose.ui.geometry.Offset(x.toFloat(), y.toFloat()),
                            size = androidx.compose.ui.geometry.Size(1f, 1f)
                        )
                    }
                }
            }
        }
    }
}

// Оптимизированная версия рендеринга
private fun renderJulia(c: Complex, w: Int, h: Int): List<List<Color>> {
    // Гарантируем минимальный размер и защищаем от некорректных значений
    val safeWidth = maxOf(1, w)
    val safeHeight = maxOf(1, h)

    val maxIter = 200 // Меньше итераций для быстрого отображения
    val result = List(safeWidth) { MutableList(safeHeight) { Color.Black } }

    val scale = 2.0 // Больший масштаб для лучшей детализации в маленьком окне
    for (xi in 0 until safeWidth) {
        val re = (xi - safeWidth/2.0) / (safeWidth/2.0) * scale
        for (yi in 0 until safeHeight) {
            val im = (yi - safeHeight/2.0) / (safeHeight/2.0) * scale
            var z = Complex(re, im)
            var iter = 0
            while (iter < maxIter && z.absoluteValue2 < 4) {
                z = z * z
                z = z + c
                iter++
            }
            val t = iter / maxIter.toFloat()
            result[xi][yi] = if (iter == maxIter) Color.Black
            else Color.hsv(t * 360f, 0.8f, 0.9f)
        }
    }
    return result
}