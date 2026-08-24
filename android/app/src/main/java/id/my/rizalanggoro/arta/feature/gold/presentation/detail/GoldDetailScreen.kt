package id.my.rizalanggoro.arta.feature.gold.presentation.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.compose.cartesian.data.lineModel
import com.patrykandpatrick.vico.compose.common.data.ExtraStore
import com.patrykandpatrick.vico.compose.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.marker.LineCartesianLayerMarkerTarget
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.common.Fill
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import id.my.rizalanggoro.arta.core.application.route.GoldRoute
import id.my.rizalanggoro.arta.core.constant.goldTypes
import id.my.rizalanggoro.arta.core.extension.toFormattedDate
import id.my.rizalanggoro.arta.core.extension.toIndonesianCurrency
import id.my.rizalanggoro.arta.core.utils.LocalBackStack
import id.my.rizalanggoro.arta.feature.gold.presentation.pricehistory.PriceRange
import id.my.rizalanggoro.arta.openapi.models.DomainGold
import id.my.rizalanggoro.arta.openapi.models.DtoPricePoint
import id.my.rizalanggoro.arta.shared.component.ArtaMiuixTheme
import id.my.rizalanggoro.arta.shared.component.ConfirmDialog
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.DropdownImpl
import top.yukonga.miuix.kmp.basic.DropdownItem
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.ListPopupColumn
import top.yukonga.miuix.kmp.basic.PopupPositionProvider
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.basic.SnackbarHost
import top.yukonga.miuix.kmp.basic.SnackbarHostState
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.icon.extended.Delete
import top.yukonga.miuix.kmp.icon.extended.Edit
import top.yukonga.miuix.kmp.icon.extended.More
import top.yukonga.miuix.kmp.preference.WindowDropdownPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.window.WindowListPopup

@Composable
fun GoldDetailScreen(
    goldId: Int,
    vm: GoldDetailVM = hiltViewModel(),
) {
    val uiState by vm.uiState.collectAsState()
    val backStack = LocalBackStack.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(goldId) {
        vm.load(goldId)
    }

    LaunchedEffect(Unit) {
        vm.effect.collect { effect ->
            when (effect) {
                is GoldDetailEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
                GoldDetailEffect.NavigateBack -> backStack.removeLastOrNull()
            }
        }
    }

    Content(
        snackbarHostState = snackbarHostState,
        uiState = uiState,
        onClickBack = { backStack.removeLastOrNull() },
        onClickEdit = { backStack.add(GoldRoute.Upsert(goldId = goldId)) },
        onDeleteRequested = vm::onDeleteRequested,
        onDismissDelete = vm::dismissDeleteDialog,
        onConfirmDelete = vm::confirmDelete,
        onSelectChartRange = vm::selectChartRange,
    )
}

@Composable
private fun Content(
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
    uiState: GoldDetailUiState = GoldDetailUiState(),
    onClickBack: () -> Unit = {},
    onClickEdit: () -> Unit = {},
    onDeleteRequested: () -> Unit = {},
    onDismissDelete: () -> Unit = {},
    onConfirmDelete: () -> Unit = {},
    onSelectChartRange: (PriceRange) -> Unit = {},
) {
    var showActions by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(state = snackbarHostState) },
        topBar = {
            SmallTopAppBar(
                title = "Detail Emas",
                navigationIcon = {
                    IconButton(onClick = onClickBack) {
                        Icon(MiuixIcons.Back, null)
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { showActions = true }) {
                            Icon(MiuixIcons.More, null)
                        }

                        if (showActions) {
                            WindowListPopup(
                                show = true,
                                onDismissRequest = { showActions = false },
                                alignment = PopupPositionProvider.Align.End,
                            ) {
                                ListPopupColumn {
                                    listOf(
                                        DropdownItem(
                                            text = "Ubah",
                                            icon = { iconModifier ->
                                                Icon(MiuixIcons.Edit, null, modifier = iconModifier)
                                            },
                                        ),
                                        DropdownItem(
                                            text = "Hapus",
                                            icon = { iconModifier ->
                                                Icon(
                                                    MiuixIcons.Delete,
                                                    null,
                                                    modifier = iconModifier,
                                                    tint = MiuixTheme.colorScheme.error
                                                )
                                            },
                                        ),
                                    ).forEachIndexed { index, item ->
                                        DropdownImpl(
                                            item = item,
                                            optionSize = 2,
                                            isSelected = false,
                                            index = index,
                                            onSelectedIndexChange = {
                                                showActions = false
                                                when (index) {
                                                    0 -> onClickEdit()
                                                    else -> onDeleteRequested()
                                                }
                                            },
                                        )
                                    }
                                }
                            }
                        }
                    }
                },
            )
        }
    ) { paddingValues ->
        val gold = uiState.gold

        when {
            uiState.isLoading && gold == null -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "Memuat...", style = MiuixTheme.textStyles.body2)
            }

            gold == null -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Data emas tidak ditemukan",
                    style = MiuixTheme.textStyles.body2,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                )
            }

            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues),
            ) {
                // Hero: nilai jual saat ini
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                    insideMargin = PaddingValues(16.dp),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "${goldTypeName(gold.type)} • ${gold.carat.toInt()}k • ${gold.grams} gr",
                            style = MiuixTheme.textStyles.footnote1,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                        )
                        Text(
                            text = uiState.sellPrice.toIndonesianCurrency(),
                            style = MiuixTheme.textStyles.title2.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = when {
                                uiState.profit > 0.0 -> "Untung +${uiState.profit.toIndonesianCurrency()}"
                                uiState.profit < 0.0 -> "Rugi ${uiState.profit.toIndonesianCurrency()}"
                                else -> "Belum ada keuntungan"
                            },
                            style = MiuixTheme.textStyles.footnote1.copy(fontWeight = FontWeight.SemiBold),
                            color = when {
                                uiState.profit > 0.0 -> MiuixTheme.colorScheme.primary
                                uiState.profit < 0.0 -> MiuixTheme.colorScheme.error
                                else -> MiuixTheme.colorScheme.onSurfaceVariantSummary
                            }
                        )
                    }
                }

                // Ringkasan investasi
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, start = 16.dp, end = 16.dp),
                ) {
                    SummaryStat(
                        label = "Harga beli",
                        value = gold.price.toIndonesianCurrency(),
                        isLast = false,
                    )
                    SummaryStat(
                        label = "Nilai jual",
                        value = uiState.sellPrice.toIndonesianCurrency(),
                        isLast = false,
                    )
                    SummaryStat(
                        label = when {
                            uiState.profit > 0.0 -> "Keuntungan"
                            uiState.profit < 0.0 -> "Kerugian"
                            else -> "Selisih"
                        },
                        value = when {
                            uiState.profit > 0.0 -> "+${uiState.profit.toIndonesianCurrency()}"
                            else -> uiState.profit.toIndonesianCurrency()
                        },
                        valueColor = when {
                            uiState.profit > 0.0 -> MiuixTheme.colorScheme.primary
                            uiState.profit < 0.0 -> MiuixTheme.colorScheme.error
                            else -> MiuixTheme.colorScheme.onSurface
                        },
                        isLast = true,
                    )
                }

                // Grafik nilai emas
                SmallTitle(
                    text = "Grafik Nilai",
                    modifier = Modifier.padding(top = 16.dp),
                    insideMargin = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp),
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                ) {
                    WindowDropdownPreference(
                        items = PriceRange.entries.map { it.shortLabel },
                        selectedIndex = PriceRange.entries.indexOf(uiState.chartRange),
                        title = "Rentang waktu",
                        enabled = !uiState.isLoadingChart,
                        onSelectedIndexChange = { index ->
                            onSelectChartRange(PriceRange.entries[index])
                        },
                    )
                }

                if (uiState.isLoadingChart) {
                    Text(
                        text = "Memuat grafik...",
                        style = MiuixTheme.textStyles.footnote2,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                } else if (uiState.chartPoints.isNotEmpty()) {
                    ItemValueChart(
                        points = uiState.chartPoints,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }

                // Rincian
                SmallTitle(
                    text = "Rincian",
                    modifier = Modifier.padding(top = 16.dp),
                    insideMargin = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp),
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                ) {
                    DetailRow(label = "Tanggal beli", value = gold.date.toFormattedDate("dd MMMM yyyy"))
                    DetailRow(label = "Berat", value = "${gold.grams} gr")
                    DetailRow(label = "Harga beli total", value = gold.price.toIndonesianCurrency())
                    DetailRow(
                        label = "Harga beli per gram",
                        value = (gold.price / gold.grams.coerceAtLeast(0.01)).toIndonesianCurrency()
                    )
                    DetailRow(label = "Catatan", value = gold.notes.ifEmpty { "-" })
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        if (uiState.showDeleteDialog) {
            ConfirmDialog(
                title = "Hapus data emas",
                description = "Anda yakin ingin menghapus data emas ini?",
                confirmText = "Hapus",
                onDismissRequest = onDismissDelete,
                onConfirmRequest = onConfirmDelete,
            )
        }
    }
}

@Composable
private fun SummaryStat(
    label: String,
    value: String,
    valueColor: Color = MiuixTheme.colorScheme.onSurface,
    isLast: Boolean = false,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = if (isLast) 10.dp else 10.dp)
    ) {
        Text(
            text = label,
            style = MiuixTheme.textStyles.footnote2,
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary
        )
        Text(
            text = value,
            style = MiuixTheme.textStyles.body1.copy(fontWeight = FontWeight.SemiBold),
            color = valueColor
        )
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    BasicComponent(
        title = value,
        summary = label,
    )
}

@Composable
private fun ItemValueChart(
    points: List<DtoPricePoint>,
    modifier: Modifier = Modifier,
) {
    val sampled = remember(points) {
        val step = maxOf(1, points.size / 400)
        points.filterIndexed { index, _ -> index % step == 0 || index == points.lastIndex }
    }

    val xAxisFormatter = remember(points) {
        CartesianValueFormatter { _, value, _ ->
            val index = value.toInt().coerceIn(0, sampled.lastIndex.coerceAtLeast(0))
            sampled.getOrNull(index)?.timestamp?.toFormattedDate("dd/MM") ?: "-"
        }
    }

    val lineColor = MiuixTheme.colorScheme.primary
    val marker = rememberDefaultCartesianMarker(
        label = rememberTextComponent(
            style = TextStyle(color = MiuixTheme.colorScheme.onPrimary, fontSize = 9.sp),
            background = rememberShapeComponent(
                fill = Fill(lineColor),
                shape = RoundedCornerShape(percent = 50)
            ),
        ),
        valueFormatter = DefaultCartesianMarker.ValueFormatter { _, targets ->
            (targets.firstOrNull() as? LineCartesianLayerMarkerTarget)
                ?.points?.firstOrNull()?.entry?.y?.toIndonesianCurrency() ?: ""
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
            valueFormatter = CartesianValueFormatter.decimal(
                decimalCount = 0,
                thousandsSeparator = ".",
            ),
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

    LaunchedEffect(sampled) {
        modelProducer.runTransaction {
            lineModel { series(y = sampled.map { it.value }) }
        }
    }

    CartesianChartHost(
        chart = chart,
        modelProducer = modelProducer,
        scrollState = rememberVicoScrollState(scrollEnabled = false),
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp),
    )
}

private fun paddedSpan(minY: Double, maxY: Double): Double =
    ((maxY - minY).coerceAtLeast(1.0)) * 0.08

private fun goldTypeName(type: String): String =
    goldTypes.firstOrNull { it.value == type }?.name ?: type

private val PriceRange.shortLabel: String
    get() = when (this) {
        PriceRange.ONE_DAY -> "1 hari"
        PriceRange.ONE_WEEK -> "1 mgg"
        PriceRange.ONE_MONTH -> "1 bln"
        PriceRange.THREE_MONTHS -> "3 bln"
        PriceRange.SIX_MONTHS -> "6 bln"
        PriceRange.ONE_YEAR -> "1 thn"
    }

@Preview(showBackground = true, name = "Gold Detail - Default")
@Composable
private fun GoldDetailPreview() {
    ArtaMiuixTheme {
        Content(
            uiState = GoldDetailUiState(
                gold = DomainGold(
                    id = 1,
                    walletId = 2,
                    date = "2026-05-25T14:38:00.000+07:00",
                    grams = 10.0,
                    price = 9000000.0,
                    type = "pure_gold",
                    carat = 24.0,
                    notes = "Contoh catatan",
                    createdAt = "",
                    updatedAt = "",
                ),
                sellPrice = 10200000.0,
                profit = 1200000.0,
            )
        )
    }
}

@Preview(showBackground = true, name = "Gold Detail - Loading")
@Composable
private fun GoldDetailLoadingPreview() {
    ArtaMiuixTheme {
        Content(uiState = GoldDetailUiState(isLoading = true))
    }
}
