package id.my.rizalanggoro.arta.feature.gold.presentation.assethistory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.compose.cartesian.data.lineModel
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.common.Fill
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.data.ExtraStore
import com.patrykandpatrick.vico.compose.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.marker.LineCartesianLayerMarkerTarget
import id.my.rizalanggoro.arta.core.extension.toFormattedDate
import id.my.rizalanggoro.arta.core.extension.toIndonesianCurrency
import id.my.rizalanggoro.arta.core.utils.LocalBackStack
import id.my.rizalanggoro.arta.feature.gold.presentation.pricehistory.PriceRange
import id.my.rizalanggoro.arta.shared.component.ArtaMiuixTheme
import id.my.rizalanggoro.arta.shared.component.ErrorPlaceholder
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.InfiniteProgressIndicator
import top.yukonga.miuix.kmp.basic.LinearProgressIndicator
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.basic.TabRowWithContour
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.preference.WindowDropdownPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun AssetHistoryScreen(
    vm: AssetHistoryVM = hiltViewModel(),
) {
    val uiState by vm.uiState.collectAsState()
    val backStack = LocalBackStack.current

    Content(
        uiState = uiState,
        onClickRetry = { vm.loadHistory() },
        onSelectRange = vm::selectRange,
        onSelectMode = vm::selectMode,
        onClickBack = { backStack.removeLastOrNull() },
    )
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    uiState: AssetHistoryUiState = AssetHistoryUiState(),
    onClickRetry: () -> Unit = {},
    onSelectRange: (PriceRange) -> Unit = {},
    onSelectMode: (AssetChartMode) -> Unit = {},
    onClickBack: () -> Unit = {},
) {
    ArtaMiuixTheme {
        Scaffold(
            topBar = {
                SmallTopAppBar(
                    title = "Riwayat Aset Emas",
                    navigationIcon = {
                        IconButton(onClick = onClickBack) {
                            Icon(MiuixIcons.Back, null)
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

                uiState.errorMessage != null && uiState.points.isEmpty() -> ErrorPlaceholder(
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

                else -> AssetChart(
                    uiState = uiState,
                    modifier = modifier.padding(innerPadding),
                    onSelectRange = onSelectRange,
                    onSelectMode = onSelectMode,
                )
            }
        }
    }
}

@Composable
private fun AssetChart(
    uiState: AssetHistoryUiState,
    modifier: Modifier = Modifier,
    onSelectRange: (PriceRange) -> Unit = {},
    onSelectMode: (AssetChartMode) -> Unit = {},
) {
    val lineColor = MiuixTheme.colorScheme.primary
    var sampledPoints by remember { mutableStateOf(emptyList<AssetPoint>()) }
    val xAxisFormatter = remember(uiState.range) {
        CartesianValueFormatter { _, value, _ ->
            val index = value.toInt().coerceIn(0, sampledPoints.lastIndex.coerceAtLeast(0))
            val pattern = if (uiState.range == PriceRange.ONE_DAY) "HH:mm" else "dd/MM"
            sampledPoints.getOrNull(index)?.timestamp?.toFormattedDate(pattern) ?: "-"
        }
    }
    val yAxisFormatter = remember(uiState.mode) {
        when (uiState.mode) {
            AssetChartMode.PROFIT_PCT -> CartesianValueFormatter { _, value, _ ->
                "${String.format(java.util.Locale.forLanguageTag("id-ID"), "%.1f", value)}%"
            }

            else -> CartesianValueFormatter.decimal(
                decimalCount = 0,
                thousandsSeparator = ".",
            )
        }
    }
    val marker = rememberDefaultCartesianMarker(
        label = rememberTextComponent(
            style = TextStyle(
                color = MiuixTheme.colorScheme.onPrimary,
                fontSize = 9.sp,
            ),
            background = rememberShapeComponent(
                fill = Fill(lineColor),
                shape = RoundedCornerShape(percent = 50)
            ),
        ),
        valueFormatter = DefaultCartesianMarker.ValueFormatter { _, targets ->
            val y = (targets.firstOrNull() as? LineCartesianLayerMarkerTarget)
                ?.points?.firstOrNull()?.entry?.y ?: return@ValueFormatter ""
            when (uiState.mode) {
                AssetChartMode.ASSET -> y.toIndonesianCurrency()
                AssetChartMode.PROFIT_RP -> y.toIndonesianCurrency()
                AssetChartMode.PROFIT_PCT -> y.formatPct()
            }
        },
        guideline = rememberLineComponent(
            fill = Fill(lineColor.copy(alpha = 0.5f)),
            thickness = 1.dp,
        ),
    )
    val chart = rememberCartesianChart(
        rememberLineCartesianLayer(
            lineProvider = LineCartesianLayer.LineProvider.series(
                listOf(
                    LineCartesianLayer.rememberLine(
                        fill = LineCartesianLayer.LineFill.single(Fill(lineColor)),
                        areaFill = LineCartesianLayer.AreaFill.single(
                            Fill(lineColor.copy(alpha = 0.25f))
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
            label = rememberAxisLabelComponent(
                style = TextStyle(
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    fontSize = 9.sp,
                )
            ),
            valueFormatter = yAxisFormatter,
            itemPlacer = VerticalAxis.ItemPlacer.count(count = { 6 }),
        ),
        bottomAxis = HorizontalAxis.rememberBottom(
            label = rememberAxisLabelComponent(
                style = TextStyle(
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    fontSize = 9.sp,
                )
            ),
            valueFormatter = xAxisFormatter
        ),
        marker = marker,
    )
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(uiState.points, uiState.mode) {
        val points = uiState.points
        if (points.isEmpty()) return@LaunchedEffect

        val step = maxOf(1, points.size / MAX_CHART_POINTS)
        val sampled = points.filterIndexed { index, _ ->
            index % step == 0 || index == points.lastIndex
        }

        sampledPoints = sampled
        modelProducer.runTransaction {
            lineModel {
                series(y = sampled.map { it.y(uiState.mode) })
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
                    text = "Total aset terakhir",
                    fontSize = 13.sp,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                )
                Text(
                    text = latestPoint.assetValue.toIndonesianCurrency(),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MiuixTheme.colorScheme.primary
                )
                Text(
                    text = "${latestPoint.profitRp.toIndonesianCurrency()} (${latestPoint.profitPct.formatPct()})",
                    fontSize = 13.sp,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                )
                Text(
                    text = "Diperbarui ${latestPoint.timestamp.toFormattedDate("dd/MM/yyyy HH:mm")}",
                    fontSize = 13.sp,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                )
            }
        }

        Card(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
            TabRowWithContour(
                tabs = AssetChartMode.entries.map { it.label },
                selectedTabIndex = AssetChartMode.entries.indexOf(uiState.mode),
                onTabSelected = { index -> onSelectMode(AssetChartMode.entries[index]) },
                modifier = Modifier.padding(12.dp),
            )
        }

        Card(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
            WindowDropdownPreference(
                items = PriceRange.entries.map { it.shortLabel },
                selectedIndex = PriceRange.entries.indexOf(uiState.range),
                title = "Rentang waktu",
                onSelectedIndexChange = { index -> onSelectRange(PriceRange.entries[index]) },
            )
        }
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

private fun AssetPoint.y(mode: AssetChartMode): Double = when (mode) {
    AssetChartMode.ASSET -> assetValue
    AssetChartMode.PROFIT_RP -> profitRp
    AssetChartMode.PROFIT_PCT -> profitPct
}

private fun Double.formatPct(): String = buildString {
    if (this@formatPct > 0) append("+")
    append(String.format(java.util.Locale.forLanguageTag("id-ID"), "%.1f", this@formatPct))
    append("%")
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

private val samplePoints = listOf(
    AssetPoint("2026-09-13T10:00:00Z", 15_000_000.0, 500_000.0, 3.4),
    AssetPoint("2026-09-14T10:00:00Z", 15_200_000.0, 700_000.0, 4.8),
    AssetPoint("2026-09-15T10:00:00Z", 14_900_000.0, 400_000.0, 2.8),
    AssetPoint("2026-09-16T10:00:00Z", 15_500_000.0, 1_000_000.0, 6.9),
    AssetPoint("2026-09-17T10:00:00Z", 15_350_000.0, 850_000.0, 5.9),
)

@Preview(showBackground = true, name = "Riwayat Aset")
@Composable
private fun AssetHistoryPreview() {
    AssetChart(
        uiState = AssetHistoryUiState(
            isLoading = false,
            points = samplePoints,
            mode = AssetChartMode.ASSET,
        )
    )
}

@Preview(showBackground = true, name = "Riwayat Aset - Untung %")
@Composable
private fun AssetHistoryPctPreview() {
    AssetChart(
        uiState = AssetHistoryUiState(
            isLoading = false,
            points = samplePoints,
            mode = AssetChartMode.PROFIT_PCT,
        )
    )
}

@Preview(showBackground = true, name = "Riwayat Aset - Loading")
@Composable
private fun AssetHistoryPreviewLoading() {
    Content(
        uiState = AssetHistoryUiState(isLoading = true),
    )
}

@Preview(showBackground = true, name = "Riwayat Aset - Error")
@Composable
private fun AssetHistoryPreviewError() {
    Content(
        uiState = AssetHistoryUiState(
            isLoading = false,
            errorMessage = "Gagal memuat riwayat aset.",
        ),
    )
}
