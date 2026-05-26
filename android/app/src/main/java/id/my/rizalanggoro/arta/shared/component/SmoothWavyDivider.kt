package id.my.rizalanggoro.arta.shared.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SmoothWavyDivider(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.outline,
    waveCount: Float = 4f,
    strokeWidth: Dp = 2.dp
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(4.dp)
    ) {
        val path = Path()
        val amplitude = 2.dp.toPx()
        val waveWidth = size.width / waveCount
        val waveCount = size.width / waveWidth

        path.moveTo(0f, size.height / 2)

        for (x in 0..size.width.toInt()) {
            val radians = (x / size.width) * waveCount * 2f * Math.PI
            val y = (amplitude * kotlin.math.sin(radians)).toFloat() + size.height / 2
            path.lineTo(x.toFloat(), y)
        }

        drawPath(
            path = path,
            color = color,
            style = Stroke(
                width = strokeWidth.toPx(),
                cap = StrokeCap.Round
            )
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun Preview() {
    SmoothWavyDivider(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.outlineVariant
    )
}