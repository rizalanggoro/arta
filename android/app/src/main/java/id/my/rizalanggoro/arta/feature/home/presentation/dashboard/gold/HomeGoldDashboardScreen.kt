package id.my.rizalanggoro.arta.feature.home.presentation.dashboard.gold

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.openapi.models.DomainGold
import id.my.rizalanggoro.arta.openapi.models.DomainWallet
import id.my.rizalanggoro.arta.openapi.models.DtoGold
import id.my.rizalanggoro.arta.shared.component.ErrorPlaceholder
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme
import kotlinx.coroutines.flow.filterIsInstance
import java.math.BigDecimal
import java.text.NumberFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.util.Locale
import kotlin.math.abs

@Composable
fun HomeGoldDashboardScreen(
    vm: GoldDashboardVM = hiltViewModel()
) {
    val uiState by vm.uiState.collectAsState()

    LaunchedEffect(Unit) {
        AppEventBus.event
            .filterIsInstance<AppEvent.GoldChanged>()
            .collect { vm.retry() }
    }

    Content(
        uiState = uiState,
        onClickRetry = vm::retry,
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun Content(
    uiState: GoldDashboardUiState = GoldDashboardUiState(),
    onClickRetry: () -> Unit = {},
) {
    when {
        uiState.isLoading -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            LoadingIndicator()
        }

        uiState.errorMessage != null -> ErrorPlaceholder(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            onClickRetry = onClickRetry
        )

        else -> LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainer)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Total asset",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = uiState.totalAsset,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Column {
                        Text(
                            text = "Harga beli ${uiState.buyPrice}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Rounded.TrendingUp,
                                null,
                                modifier = Modifier.size(MaterialTheme.typography.bodyMedium.fontSize.value.dp),
                                tint = MaterialTheme.colorScheme.outline
                            )
                            Text(
                                text = "${uiState.profit} (74%)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }

            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp)
                ) {
                    SummaryCard(
                        modifier = Modifier.weight(1f),
                        title = "Total berat",
                        value = uiState.totalWeight
                    )
                    SummaryCard(
                        modifier = Modifier.weight(1f),
                        title = "Total emas",
                        value = uiState.totalGoldItems
                    )
                }
            }

            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 8.dp)
                ) {
                    SummaryCard(
                        modifier = Modifier.weight(1f),
                        title = "Harga dollar terbaru",
                        value = uiState.latestDollarPrice
                    )
                    SummaryCard(
                        modifier = Modifier.weight(1f),
                        title = "Harga emas / gram",
                        value = uiState.latestGoldPricePerGramIdr
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                    shape = CardDefaults.shape,
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                    ) {
                        Text(
                            text = "Emas Terbaru",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            uiState.recentGolds.take(5).forEachIndexed { index, gold ->
                                GoldRow(gold = gold)
                                if (index < uiState.recentGolds.take(5).lastIndex) {
                                    HorizontalDivider()
                                }
                            }
                        }

                        Button(
                            onClick = {}, modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Lihat lainnya")
                                Icon(
                                    Icons.AutoMirrored.Rounded.ArrowForward,
                                    null
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier, colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(text = value, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun GoldRow(
    gold: DtoGold,
    modifier: Modifier = Modifier,
) {
    val data = gold.data

    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = data.notes.ifBlank { "Emas #${data.id}" },
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = listOfNotNull(
                    data.type.replace('_', ' '),
                    formatDate(data.date),
                ).joinToString(" · "),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "Jual: ${formatMoney(gold.sellPrice)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "${formatWeight(data.grams)} · ${formatMoney(data.price)}",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = "Profit: ${formatSignedMoney(gold.profit.toDouble())}",
                style = MaterialTheme.typography.bodySmall,
                color = if (gold.profit.toDouble() >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
            )
        }
    }
}

@Preview(
    showBackground = true,
    wallpaper = Wallpapers.GREEN_DOMINATED_EXAMPLE
)
@Composable
private fun HomeGoldDashboardPreviewDefault() {
    ArtaTheme {
        Content(
            uiState = GoldDashboardUiState(
                selectedWallet = DomainWallet(
                    createdAt = "",
                    id = 1,
                    name = "Emasku",
                    type = "gold_savings",
                    updatedAt = "",
                    userId = 1
                ),
                totalAsset = "Rp 35.250.000",
                buyPrice = "Rp 30.000.000",
                profit = "Rp 5.250.000",
                totalWeight = "15.00 g",
                totalGoldItems = "5 item",
                latestDollarPrice = "Rp 16.200",
                latestGoldPricePerGramIdr = "Rp 2.350.000",
                recentGolds = listOf(
                    DtoGold(
                        data = DomainGold(
                            id = 1,
                            walletId = 1,
                            date = "2026-05-16T00:00:00Z",
                            grams = BigDecimal.valueOf(3.0),
                            price = BigDecimal.valueOf(4800000.0),
                            type = "pure_gold",
                            carat = BigDecimal.valueOf(24.0),
                            notes = "Emas 24K",
                            createdAt = "",
                            updatedAt = ""
                        ),
                        profit = BigDecimal.valueOf(250000.0),
                        sellPrice = BigDecimal.valueOf(5050000.0)
                    ),
                    DtoGold(
                        data = DomainGold(
                            id = 2,
                            walletId = 1,
                            date = "2026-05-15T00:00:00Z",
                            grams = BigDecimal.valueOf(2.5),
                            price = BigDecimal.valueOf(4000000.0),
                            type = "pure_gold",
                            carat = BigDecimal.valueOf(24.0),
                            notes = "Emas 24K",
                            createdAt = "",
                            updatedAt = ""
                        ),
                        profit = BigDecimal.valueOf(150000.0),
                        sellPrice = BigDecimal.valueOf(4150000.0)
                    ),
                    DtoGold(
                        data = DomainGold(
                            id = 3,
                            walletId = 1,
                            date = "2026-05-13T00:00:00Z",
                            grams = BigDecimal.valueOf(1.75),
                            price = BigDecimal.valueOf(3200000.0),
                            type = "jewelry",
                            carat = BigDecimal.valueOf(18.0),
                            notes = "Cincin emas",
                            createdAt = "",
                            updatedAt = ""
                        ),
                        profit = BigDecimal.valueOf(-120000.0),
                        sellPrice = BigDecimal.valueOf(3080000.0)
                    ),
                    DtoGold(
                        data = DomainGold(
                            id = 4,
                            walletId = 1,
                            date = "2026-05-10T00:00:00Z",
                            grams = BigDecimal.valueOf(5.0),
                            price = BigDecimal.valueOf(7500000.0),
                            type = "pure_gold",
                            carat = BigDecimal.valueOf(24.0),
                            notes = "Emas 24K",
                            createdAt = "",
                            updatedAt = ""
                        ),
                        profit = BigDecimal.valueOf(300000.0),
                        sellPrice = BigDecimal.valueOf(7800000.0)
                    ),
                    DtoGold(
                        data = DomainGold(
                            id = 5,
                            walletId = 1,
                            date = "2026-05-08T00:00:00Z",
                            grams = BigDecimal.valueOf(2.75),
                            price = BigDecimal.valueOf(4500000.0),
                            type = "jewelry",
                            carat = BigDecimal.valueOf(18.0),
                            notes = "Kalung emas",
                            createdAt = "",
                            updatedAt = ""
                        ),
                        profit = BigDecimal.valueOf(-90000.0),
                        sellPrice = BigDecimal.valueOf(4410000.0)
                    ),
                ),
                isLoading = false,
                errorMessage = null,
            )
        )
    }
}

@Preview(showBackground = true, name = "Dashboard Emas - Loading")
@Composable
private fun HomeGoldDashboardPreviewLoading() {
    ArtaTheme {
        Content(
            uiState = GoldDashboardUiState(
                totalAsset = "Rp 0",
                buyPrice = "Rp 0",
                profit = "Rp 0",
                totalWeight = "0.00 g",
                totalGoldItems = "0 item",
                latestDollarPrice = "Rp 0",
                latestGoldPricePerGramIdr = "Rp 0",
                recentGolds = emptyList(),
                isLoading = true,
                errorMessage = null,
            ),
        )
    }
}

@Preview(showBackground = true, name = "Dashboard Emas - Error")
@Composable
private fun HomeGoldDashboardPreviewError() {
    ArtaTheme {
        Content(
            uiState = GoldDashboardUiState(
                totalAsset = "Rp 0",
                buyPrice = "Rp 0",
                profit = "Rp 0",
                totalWeight = "0.00 g",
                totalGoldItems = "0 item",
                latestDollarPrice = "Rp 0",
                latestGoldPricePerGramIdr = "Rp 0",
                recentGolds = emptyList(),
                isLoading = false,
                errorMessage = "Gagal memuat dashboard emas.",
            ),
        )
    }
}

private fun formatMoney(value: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID")).apply {
        maximumFractionDigits = 0
    }
    return "Rp ${formatter.format(value.toLong())}"
}

private fun formatWeight(value: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID")).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }
    return "${formatter.format(value)} g"
}

private fun formatWeight(value: BigDecimal): String = formatWeight(value.toDouble())

private fun formatMoney(value: BigDecimal): String = formatMoney(value.toDouble())

private fun formatSignedMoney(value: Double): String {
    val sign = if (value >= 0) "+" else "-"
    return "$sign${formatMoney(abs(value))}"
}

private fun formatDate(value: String): String {
    if (value.isBlank()) return "-"

    val formatted = runCatching { OffsetDateTime.parse(value) }.getOrNull()?.toLocalDate()
        ?: runCatching { LocalDateTime.parse(value).toLocalDate() }.getOrNull()
        ?: runCatching { LocalDate.parse(value) }.getOrNull()

    return formatted?.toString() ?: value
}
