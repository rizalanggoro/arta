package id.my.rizalanggoro.arta.feature.gold.presentation.pricehistory

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.patrykandpatrick.vico.core.common.shader.ShaderProvider
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import id.my.rizalanggoro.arta.core.application.route.GoldRoute
import id.my.rizalanggoro.arta.core.extension.toAmericanCurrency
import id.my.rizalanggoro.arta.core.extension.toFormattedDate
import id.my.rizalanggoro.arta.core.extension.toIndonesianCurrency
import id.my.rizalanggoro.arta.core.extension.toMillis
import id.my.rizalanggoro.arta.core.utils.LocalBackStack
import id.my.rizalanggoro.arta.openapi.models.DtoPricePoint
import id.my.rizalanggoro.arta.shared.component.ErrorPlaceholder
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceHistoryScreen(
    vm: PriceHistoryVM = hiltViewModel(),
) {
    val backStack = LocalBackStack.current
    val uiState by vm.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                title = {
                    Text(text = uiState.type.title)
                },
                navigationIcon = {
                    IconButton(onClick = { backStack.removeLastOrNull() }) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            null
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Content(
            modifier = Modifier.padding(innerPadding),
            uiState = uiState,
            onClickRetry = { vm.loadHistory() },
            onSelectRange = vm::selectRange,
        )
    }
}

@Composable
private fun Content(
    uiState: PriceHistoryUiState = PriceHistoryUiState(),
    modifier: Modifier = Modifier,
    onClickRetry: () -> Unit = {},
    onSelectRange: (PriceRange) -> Unit = {},
) {
    when {
        uiState.isLoading && uiState.points.isEmpty() -> Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            LoadingIndicator()
        }

        uiState.errorMessage != null -> ErrorPlaceholder(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            onClickRetry = onClickRetry
        )

        uiState.points.isEmpty() -> Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Belum ada data. Riwayat akan muncul setelah server mengumpulkan data harga.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }

        else -> PriceChart(
            uiState = uiState,
            modifier = modifier,
            onSelectRange = onSelectRange,
        )
    }
}

@Composable
private fun PriceChart(
    uiState: PriceHistoryUiState,
    modifier: Modifier = Modifier,
    onSelectRange: (PriceRange) -> Unit = {},
) {
    val lineColor = MaterialTheme.colorScheme.primary
    var sampledPoints by remember { mutableStateOf(emptyList<DtoPricePoint>()) }
    val xAxisFormatter = remember {
        CartesianValueFormatter { _, value, _ ->
            val index = value.toInt().coerceIn(0, sampledPoints.lastIndex.coerceAtLeast(0))
            sampledPoints.getOrNull(index)?.timestamp?.toFormattedDate("dd/MM HH:mm") ?: " "
        }
    }
    val chart = rememberCartesianChart(
        rememberLineCartesianLayer(
            lineProvider = LineCartesianLayer.LineProvider.series(
                listOf(
                    LineCartesianLayer.rememberLine(
                        fill = LineCartesianLayer.LineFill.single(fill(lineColor)),
                        areaFill = LineCartesianLayer.AreaFill.single(
                            fill(
                                ShaderProvider.verticalGradient(
                                    intArrayOf(
                                        lineColor.copy(alpha = 0.25f).toArgb(),
                                        Color.Transparent.toArgb(),
                                    )
                                )
                            )
                        ),
                        pointConnector = LineCartesianLayer.PointConnector.cubic(),
                    )
                )
            ),
            rangeProvider = object : CartesianLayerRangeProvider {
                override fun getMinY(minY: Double, maxY: Double, extraStore: ExtraStore): Double =
                    minY - paddedSpan(minY, maxY)

                override fun getMaxY(minY: Double, maxY: Double, extraStore: ExtraStore): Double =
                    maxY + paddedSpan(minY, maxY)
            },
        ),
        startAxis = VerticalAxis.rememberStart(
            valueFormatter = CartesianValueFormatter.decimal(DecimalFormat("#,##0.##"))
        ),
        bottomAxis = HorizontalAxis.rememberBottom(
            valueFormatter = xAxisFormatter
        ),
    )
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(uiState.points) {
        val points = uiState.points
        if (points.isEmpty()) return@LaunchedEffect

        val step = maxOf(1, points.size / MAX_CHART_POINTS)
        val sampled = points.filterIndexed { index, _ ->
            index % step == 0 || index == points.lastIndex
        }

        sampledPoints = sampled
        modelProducer.runTransaction {
            lineSeries {
                series(y = sampled.map { it.value })
            }
        }
    }

    val latestPoint = uiState.points.last()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Nilai terakhir",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = latestPoint.value.formatValue(uiState.type),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = "Diperbarui ${latestPoint.timestamp.toFormattedDate("dd/MM/yyyy HH:mm")}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        Text(
            text = "Riwayat ${uiState.range.label} terakhir",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PriceRange.entries.forEach { range ->
                FilterChip(
                    selected = range == uiState.range,
                    onClick = { onSelectRange(range) },
                    label = { Text(text = range.label.replaceFirstChar { it.uppercase() }) },
                )
            }
        }
        CartesianChartHost(
            chart = chart,
            modelProducer = modelProducer,
            scrollState = rememberVicoScrollState(scrollEnabled = false),
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp),
        )
    }
}

private const val MAX_CHART_POINTS = 400
private const val Y_RANGE_PADDING = 0.08

private fun paddedSpan(minY: Double, maxY: Double): Double =
    (maxY - minY).coerceAtLeast(1.0) * Y_RANGE_PADDING

private val GoldRoute.PriceHistoryType.title: String
    get() = when (this) {
        GoldRoute.PriceHistoryType.GOLD -> "Harga Emas Dunia"
        GoldRoute.PriceHistoryType.FX -> "Nilai Dollar"
    }

private fun Double.formatValue(type: GoldRoute.PriceHistoryType): String =
    when (type) {
        GoldRoute.PriceHistoryType.GOLD -> this.toAmericanCurrency()
        GoldRoute.PriceHistoryType.FX -> this.toIndonesianCurrency()
    }

private val samplePoints = listOf(
    DtoPricePoint(timestamp = "2026-08-09T00:10:00Z", value = 3100.0),
    DtoPricePoint(timestamp = "2026-08-10T12:00:00Z", value = 3140.5),
    DtoPricePoint(timestamp = "2026-08-11T18:30:00Z", value = 3125.0),
    DtoPricePoint(timestamp = "2026-08-12T09:15:00Z", value = 3180.25),
    DtoPricePoint(timestamp = "2026-08-13T21:45:00Z", value = 3205.75),
    DtoPricePoint(timestamp = "2026-08-14T06:00:00Z", value = 3190.0),
    DtoPricePoint(timestamp = "2026-08-15T14:00:00Z", value = 3220.5),
)

@Preview(showBackground = true, name = "Riwayat Harga Emas")
@Composable
private fun PriceHistoryPreview() {
    ArtaTheme {
        PriceChart(
            uiState = PriceHistoryUiState(
                type = GoldRoute.PriceHistoryType.GOLD,
                isLoading = false,
                points = samplePoints,
            )
        )
    }
}

@Preview(showBackground = true, name = "Riwayat Harga - Loading")
@Composable
private fun PriceHistoryPreviewLoading() {
    ArtaTheme {
        Content(
            uiState = PriceHistoryUiState(isLoading = true),
        )
    }
}

@Preview(showBackground = true, name = "Riwayat Harga - Error")
@Composable
private fun PriceHistoryPreviewError() {
    ArtaTheme {
        Content(
            uiState = PriceHistoryUiState(
                isLoading = false,
                errorMessage = "Gagal memuat riwayat harga.",
            ),
        )
    }
}