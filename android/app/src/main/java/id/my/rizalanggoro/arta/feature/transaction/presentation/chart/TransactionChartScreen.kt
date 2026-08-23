package id.my.rizalanggoro.arta.feature.transaction.presentation.chart

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patrykandpatrick.vico.compose.common.Fill
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.pie.PieChart
import com.patrykandpatrick.vico.compose.pie.PieChartHost
import com.patrykandpatrick.vico.compose.pie.rememberPieChart
import com.patrykandpatrick.vico.compose.pie.data.PieChartModelProducer
import com.patrykandpatrick.vico.compose.pie.data.PieValueFormatter
import com.patrykandpatrick.vico.compose.pie.data.pieModel
import androidx.hilt.navigation.compose.hiltViewModel
import id.my.rizalanggoro.arta.core.constant.TransactionTimeRangeType
import id.my.rizalanggoro.arta.core.constant.formatTimeRangeLabel
import id.my.rizalanggoro.arta.core.application.route.TransactionRoute
import id.my.rizalanggoro.arta.core.extension.toIndonesianCurrency
import id.my.rizalanggoro.arta.core.utils.LocalBackStack
import id.my.rizalanggoro.arta.openapi.models.DomainCategory
import id.my.rizalanggoro.arta.openapi.models.DomainTransaction
import id.my.rizalanggoro.arta.openapi.models.DtoTransaction
import id.my.rizalanggoro.arta.shared.component.ArtaMiuixTheme
import id.my.rizalanggoro.arta.shared.component.EmptyPlaceholder
import id.my.rizalanggoro.arta.shared.component.ErrorPlaceholder
import id.my.rizalanggoro.arta.shared.component.TransactionListItem
import androidx.compose.ui.text.TextStyle
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.InfiniteProgressIndicator
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.icon.extended.ChevronBackward
import top.yukonga.miuix.kmp.icon.extended.ChevronForward
import top.yukonga.miuix.kmp.theme.MiuixTheme
import kotlin.math.atan2
import kotlin.math.hypot
import kotlin.math.min

private val piePalette = listOf(
    Color(0xFF3482FF),
    Color(0xFF34B27B),
    Color(0xFFF2A93B),
    Color(0xFFE5566D),
    Color(0xFF8A63D2),
    Color(0xFF3BB8C4),
    Color(0xFFC46AE0),
    Color(0xFF7E8894),
)

private data class PieSlice(
    val categoryId: Int,
    val label: String,
    val value: Double,
    val color: Color,
)

@Composable
fun TransactionChartScreen(
    vm: TransactionChartVM,
) {
    val uiState by vm.uiState.collectAsState()
    val backStack = LocalBackStack.current

    Content(
        uiState = uiState,
        type = vm.chartType,
        onCategorySelected = vm::onCategorySelected,
        onClickBack = { backStack.removeLastOrNull() },
        onClickTransaction = {
            backStack.add(
                TransactionRoute.Detail(transactionId = it.id)
            )
        },
    )
}

@Composable
private fun Content(
    uiState: TransactionChartUiState = TransactionChartUiState(),
    type: String = "expense",
    onCategorySelected: (Int?) -> Unit = {},
    onClickBack: () -> Unit = {},
    onClickTransaction: (DomainTransaction) -> Unit = {},
) {
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = when (type) {
                    "income" -> "Penggunaan Pemasukan"
                    else -> "Penggunaan Pengeluaran"
                },
                navigationIcon = {
                    IconButton(onClick = onClickBack) {
                        Icon(MiuixIcons.Back, null)
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                InfiniteProgressIndicator(color = MiuixTheme.colorScheme.primary)
            }

            uiState.errorMessage != null -> ErrorPlaceholder(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(16.dp),
                message = uiState.errorMessage,
            )

            else -> {
                val slices = rememberSlices(uiState.transactions)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(paddingValues),
                ) {
                    when {
                        slices.isEmpty() -> EmptyPlaceholder(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp)
                        )

                        else -> Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            PieChart(
                                slices = slices,
                                selectedIndex = slices.indexOfFirst {
                                    it.categoryId == uiState.selectedCategoryId
                                }.takeIf { it >= 0 },
                                onSliceTap = { index ->
                                    onCategorySelected(index?.let(slices::get)?.categoryId)
                                },
                                modifier = Modifier.size(220.dp),
                            )
                        }
                    }

                    if (slices.isNotEmpty()) {
                        SmallTitle(
                            text = "Kategori",
                            insideMargin = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        )

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                        ) {
                            slices.forEach { slice ->
                                val isSelected = slice.categoryId == uiState.selectedCategoryId

                                BasicComponent(
                                    title = slice.label,
                                    startAction = {
                                        Box(
                                            modifier = Modifier
                                                .padding(end = 8.dp)
                                                .size(14.dp)
                                                .clip(CircleShape)
                                                .background(slice.color)
                                        )
                                    },
                                    endActions = {
                                        Text(
                                            text = slice.value.toIndonesianCurrency(),
                                            style = MiuixTheme.textStyles.footnote1,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    },
                                    onClick = {
                                        onCategorySelected(
                                            when {
                                                isSelected -> null
                                                else -> slice.categoryId
                                            }
                                        )
                                    },
                                    insideMargin = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                                    modifier = Modifier.then(
                                        when {
                                            isSelected -> Modifier.background(
                                                MiuixTheme.colorScheme.secondaryContainer.copy(
                                                    alpha = 0.5f
                                                )
                                            )

                                            else -> Modifier
                                        }
                                    )
                                )
                            }
                        }
                    }

                    val selectedTransactions = remember(
                        uiState.transactions,
                        uiState.selectedCategoryId
                    ) {
                        uiState.transactions.filter {
                            it.category?.id == uiState.selectedCategoryId
                        }
                    }

                    when {
                        uiState.selectedCategoryId == null -> Text(
                            text = "Tap bagian diagram untuk melihat transaksinya",
                            style = MiuixTheme.textStyles.footnote1,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 24.dp)
                        )

                        selectedTransactions.isEmpty() -> Text(
                            text = "Tidak ada transaksi pada kategori ini",
                            style = MiuixTheme.textStyles.footnote1,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp)
                        )

                        else -> {
                            SmallTitle(
                                text = selectedTransactions.firstOrNull()?.category?.name ?: "",
                                insideMargin = PaddingValues(start = 16.dp, top = 24.dp, bottom = 8.dp),
                            )

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                            ) {
                                selectedTransactions.forEachIndexed { index, transaction ->
                                    TransactionListItem(
                                        transaction = transaction.data,
                                        category = transaction.category!!,
                                        index = index,
                                        size = selectedTransactions.size,
                                        onClick = onClickTransaction,
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

private val sampleCategories = listOf(
    DomainCategory(
        id = 1,
        name = "Makanan",
        type = "expense",
        createdAt = "2026-08-01T00:00:00Z",
        updatedAt = "2026-08-01T00:00:00Z",
    ),
    DomainCategory(
        id = 2,
        name = "Transportasi",
        type = "expense",
        createdAt = "2026-08-01T00:00:00Z",
        updatedAt = "2026-08-01T00:00:00Z",
    ),
    DomainCategory(
        id = 3,
        name = "Hiburan",
        type = "expense",
        createdAt = "2026-08-01T00:00:00Z",
        updatedAt = "2026-08-01T00:00:00Z",
    ),
)

private fun sampleTransaction(
    id: Int,
    amount: Double,
    category: DomainCategory,
): DtoTransaction = DtoTransaction(
    data = DomainTransaction(
        amount = amount,
        categoryId = category.id,
        createdAt = "2026-08-20T10:00:00Z",
        date = "2026-08-20T00:00:00Z",
        description = "Contoh transaksi",
        id = id,
        updatedAt = "2026-08-20T10:00:00Z",
        walletId = 1,
    ),
    category = category,
)

private val sampleTransactions = listOf(
    sampleTransaction(1, 50000.0, sampleCategories[0]),
    sampleTransaction(2, 35000.0, sampleCategories[0]),
    sampleTransaction(3, 20000.0, sampleCategories[1]),
    sampleTransaction(4, 15000.0, sampleCategories[2]),
)

@Preview(showBackground = true, name = "Chart - Kategori Dipilih")
@Composable
private fun ChartSelectedPreview() {
    ArtaMiuixTheme {
        Content(
            uiState = TransactionChartUiState(
                isLoading = false,
                transactions = sampleTransactions,
                selectedCategoryId = 1,
            ),
            type = "expense",
        )
    }
}

@Preview(showBackground = true, name = "Chart - Tanpa Seleksi")
@Composable
private fun ChartNoSelectionPreview() {
    ArtaMiuixTheme {
        Content(
            uiState = TransactionChartUiState(
                isLoading = false,
                transactions = sampleTransactions,
            ),
            type = "expense",
        )
    }
}

@Preview(showBackground = true, name = "Chart - Loading")
@Composable
private fun ChartLoadingPreview() {
    ArtaMiuixTheme {
        Content(
            uiState = TransactionChartUiState(isLoading = true),
            type = "expense",
        )
    }
}

@Preview(showBackground = true, name = "Chart - Error")
@Composable
private fun ChartErrorPreview() {
    ArtaMiuixTheme {
        Content(
            uiState = TransactionChartUiState(
                isLoading = false,
                errorMessage = "Gagal memuat penggunaan uang.",
            ),
            type = "expense",
        )
    }
}

@Preview(showBackground = true, name = "Chart - Kosong")
@Composable
private fun ChartEmptyPreview() {
    ArtaMiuixTheme {
        Content(
            uiState = TransactionChartUiState(
                isLoading = false,
                transactions = emptyList(),
            ),
            type = "income",
        )
    }
}

@Composable
private fun rememberSlices(transactions: List<DtoTransaction>): List<PieSlice> {
    return remember(transactions) {
        transactions
            .groupBy { it.category?.id to it.category?.name }
            .mapNotNull { (key, list) ->
                val id = key.first ?: return@mapNotNull null
                PieSlice(
                    categoryId = id,
                    label = key.second ?: "-",
                    value = list.sumOf { it.data.amount },
                    color = Color.Transparent,
                )
            }
            .sortedByDescending { it.value }
            .mapIndexed { index, slice ->
                slice.copy(color = piePalette[index % piePalette.size])
            }
    }
}

@Composable
private fun PieChart(
    slices: List<PieSlice>,
    selectedIndex: Int?,
    onSliceTap: (Int?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val producer = remember { PieChartModelProducer() }

    LaunchedEffect(slices) {
        if (slices.isEmpty()) return@LaunchedEffect
        producer.runTransaction {
            pieModel { series(slices.map { it.value }) }
        }
    }

    val total = slices.sumOf { it.value }.coerceAtLeast(1e-9)
    val chart = rememberPieChart(
        sliceProvider = PieChart.SliceProvider.series(
            slices.map { slice ->
                PieChart.Slice(
                    fill = Fill(slice.color),
                    label = PieChart.SliceLabel.Outside(
                        textComponent = rememberTextComponent(
                            style = MiuixTheme.textStyles.footnote1.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MiuixTheme.colorScheme.onBackground,
                            )
                        ),
                        lineColor = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    )
                )
            }
        ),
        startAngle = -90f,
        valueFormatter = PieValueFormatter { context, value, _ ->
            "${(value / context.model.sum * 100).toInt()}%"
        },
    )

    Box(modifier, contentAlignment = Alignment.Center) {
        PieChartHost(
            chart = chart,
            modelProducer = producer,
            modifier = Modifier.fillMaxSize(),
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .pointerInput(slices, selectedIndex) {
                    detectTapGestures { offset ->
                        if (slices.isEmpty()) return@detectTapGestures

                        val center = Offset(size.width / 2f, size.height / 2f)
                        val dx = offset.x - center.x
                        val dy = offset.y - center.y

                        var angle = Math.toDegrees(
                            atan2(dy.toDouble(), dx.toDouble())
                        )
                        angle = (angle + 90.0 + 360.0) % 360.0

                        var acc = 0.0
                        var tapped: Int? = null
                        slices.forEachIndexed { index, slice ->
                            val sweep = slice.value / total * 360.0
                            if (angle >= acc && angle < acc + sweep) tapped = index
                            acc += sweep
                        }

                        onSliceTap(
                            when {
                                tapped == selectedIndex -> null
                                else -> tapped
                            }
                        )
                    }
                }
        )

        if (selectedIndex != null && selectedIndex < slices.size) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(100))
                    .background(MiuixTheme.colorScheme.surfaceContainer.copy(alpha = 0.9f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = slices[selectedIndex].label,
                        style = MiuixTheme.textStyles.footnote1,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = slices[selectedIndex].value.toIndonesianCurrency(),
                        style = MiuixTheme.textStyles.body2,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}





