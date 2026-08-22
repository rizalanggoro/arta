package id.my.rizalanggoro.arta.feature.gold.presentation.pricehistory

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.component.shapeComponent
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.cartesian.marker.LineCartesianLayerMarkerTarget
import com.patrykandpatrick.vico.core.common.Insets
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.patrykandpatrick.vico.core.common.shader.ShaderProvider
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import id.my.rizalanggoro.arta.core.application.route.GoldRoute
import id.my.rizalanggoro.arta.core.extension.toAmericanCurrency
import id.my.rizalanggoro.arta.core.extension.toFormattedDate
import id.my.rizalanggoro.arta.core.extension.toIndonesianCurrency
import id.my.rizalanggoro.arta.core.utils.LocalBackStack
import id.my.rizalanggoro.arta.openapi.models.DtoPricePoint
import id.my.rizalanggoro.arta.shared.component.ErrorPlaceholder
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.InfiniteProgressIndicator
import top.yukonga.miuix.kmp.basic.LinearProgressIndicator
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.TabRow
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.darkColorScheme
import top.yukonga.miuix.kmp.theme.lightColorScheme
import java.text.DecimalFormat

@Composable
fun PriceHistoryScreen(
    vm: PriceHistoryVM = hiltViewModel(),
) {
    val uiState by vm.uiState.collectAsState()
    val backStack = LocalBackStack.current

    Content(
        uiState = uiState,
        onClickRetry = { vm.loadHistory() },
        onSelectRange = vm::selectRange,
        onClickBack = { backStack.removeLastOrNull() },
    )
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    uiState: PriceHistoryUiState = PriceHistoryUiState(),
    onClickRetry: () -> Unit = {},
    onSelectRange: (PriceRange) -> Unit = {},
    onClickBack: () -> Unit = {},
) {
    MiuixTheme(
        colors = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = uiState.type.title,
                    navigationIcon = {
                        IconButton(onClick = onClickBack) {
                            Icon(
                                MiuixIcons.Back,
                                null
                            )
                        }
                    },
                )
            },
        ) { innerPadding ->
            when {
                uiState.isLoading && uiState.points.isEmpty() -> Box(
                    modifier = modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    InfiniteProgressIndicator(color = MiuixTheme.colorScheme.primary)
                }

                uiState.errorMessage != null -> ErrorPlaceholder(
                    modifier = modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .padding(16.dp),
                    onClickRetry = onClickRetry
                )

                uiState.points.isEmpty() -> Box(
                    modifier = modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Belum ada data. Riwayat akan muncul setelah server mengumpulkan data harga.",
                        fontSize = 14.sp,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }

                else -> PriceChart(
                    uiState = uiState,
                    modifier = modifier.padding(innerPadding),
                    onSelectRange = onSelectRange,
                )
            }
        }
    }
}

@Composable
private fun PriceChart(
    uiState: PriceHistoryUiState,
    modifier: Modifier = Modifier,
    onSelectRange: (PriceRange) -> Unit = {},
) {
    val lineColor = MiuixTheme.colorScheme.primary
    var sampledPoints by remember { mutableStateOf(emptyList<DtoPricePoint>()) }
    val xAxisFormatter = remember(uiState.range) {
        CartesianValueFormatter { _, value, _ ->
            val index = value.toInt().coerceIn(0, sampledPoints.lastIndex.coerceAtLeast(0))
            val pattern = if (uiState.range == PriceRange.ONE_DAY) "HH:mm" else "dd/MM"
            sampledPoints.getOrNull(index)?.timestamp?.toFormattedDate(pattern) ?: " "
        }
    }
    val marker = rememberDefaultCartesianMarker(
        label = rememberTextComponent(
            color = MiuixTheme.colorScheme.onPrimary,
            textSize = 9.sp,
            background = shapeComponent(fill = fill(lineColor), shape = CorneredShape.Pill),
            padding = Insets(allDp = 6f),
            margins = Insets(allDp = 4f),
        ),
        valueFormatter = DefaultCartesianMarker.ValueFormatter { _, targets ->
            (targets.firstOrNull() as? LineCartesianLayerMarkerTarget)?.points
                ?.firstOrNull()?.entry?.y?.formatValue(uiState.type) ?: ""
        },
        guideline = rememberLineComponent(
            fill = fill(lineColor.copy(alpha = 0.5f)),
            thickness = 1.dp,
        ),
    )
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
            label = rememberAxisLabelComponent(textSize = 9.sp),
            valueFormatter = CartesianValueFormatter.decimal(DecimalFormat("#,##0.##")),
            itemPlacer = VerticalAxis.ItemPlacer.count(count = { 8 }),
        ),
        bottomAxis = HorizontalAxis.rememberBottom(
            label = rememberAxisLabelComponent(textSize = 9.sp),
            valueFormatter = xAxisFormatter
        ),
        marker = marker,
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
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.padding(start = 16.dp, top = 12.dp, end = 16.dp),
            insideMargin = PaddingValues(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Nilai terakhir",
                    fontSize = 13.sp,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                )
                Text(
                    text = latestPoint.value.formatValue(uiState.type),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MiuixTheme.colorScheme.primary
                )
                Text(
                    text = "Diperbarui ${latestPoint.timestamp.toFormattedDate("dd/MM/yyyy HH:mm")}",
                    fontSize = 13.sp,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                )
            }
        }

        TabRow(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
            tabs = PriceRange.entries.map { it.shortLabel },
            selectedTabIndex = PriceRange.entries.indexOf(uiState.range),
            onTabSelected = { index -> onSelectRange(PriceRange.entries[index]) },
        )
        if (uiState.isLoading) {
            LinearProgressIndicator(
                progress = null,
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp)
                    .fillMaxWidth(),
            )
        }
        CartesianChartHost(
            chart = chart,
            modelProducer = modelProducer,
            scrollState = rememberVicoScrollState(scrollEnabled = false),
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
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

private val PriceRange.shortLabel: String
    get() = when (this) {
        PriceRange.ONE_DAY -> "1 hari"
        PriceRange.ONE_WEEK -> "1 mgg"
        PriceRange.ONE_MONTH -> "1 bln"
        PriceRange.THREE_MONTHS -> "3 bln"
        PriceRange.SIX_MONTHS -> "6 bln"
        PriceRange.ONE_YEAR -> "1 thn"
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
    PriceChart(
        uiState = PriceHistoryUiState(
            type = GoldRoute.PriceHistoryType.GOLD,
            isLoading = false,
            points = samplePoints,
        )
    )
}

@Preview(showBackground = true, name = "Riwayat Harga - Loading")
@Composable
private fun PriceHistoryPreviewLoading() {
    Content(
        uiState = PriceHistoryUiState(isLoading = true),
    )
}

@Preview(showBackground = true, name = "Riwayat Harga - Error")
@Composable
private fun PriceHistoryPreviewError() {
    Content(
        uiState = PriceHistoryUiState(
            isLoading = false,
            errorMessage = "Gagal memuat riwayat harga.",
        ),
    )
}
