package id.my.rizalanggoro.arta.feature.home.presentation.dashboard.gold

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.my.rizalanggoro.arta.domain.Gold
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme
import java.text.NumberFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.util.Locale

@Composable
fun HomeGoldDashboardScreen(
    vm: GoldDashboardVM = viewModel(factory = GoldDashboardVM.Factory)
) {
    val uiState by vm.uiState.collectAsState()

    Content(
        activeWalletName = uiState.activeWalletName,
        totalAsset = uiState.totalAsset,
        buyPrice = uiState.buyPrice,
        profit = uiState.profit,
        totalWeight = uiState.totalWeight,
        totalGoldItems = uiState.totalGoldItems,
        latestDollarPrice = uiState.latestDollarPrice,
        latestGoldPricePerGramIdr = uiState.latestGoldPricePerGramIdr,
        recentGolds = uiState.recentGolds,
        isLoading = uiState.isLoading,
        errorMessage = uiState.errorMessage,
        onRetry = vm::retry,
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun Content(
    activeWalletName: String,
    totalAsset: String,
    buyPrice: String,
    profit: String,
    totalWeight: String,
    totalGoldItems: String,
    latestDollarPrice: String,
    latestGoldPricePerGramIdr: String,
    recentGolds: List<Gold>,
    isLoading: Boolean,
    errorMessage: String?,
    onRetry: () -> Unit = {},
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = activeWalletName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Total asset",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(text = totalAsset, style = MaterialTheme.typography.headlineMedium)
                    Text(text = "Harga beli: $buyPrice")
                    Text(text = "Profit: $profit")
                }
            }
        }

        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                SummaryCard(
                    modifier = Modifier.weight(1f),
                    title = "Total berat",
                    value = totalWeight
                )
                SummaryCard(
                    modifier = Modifier.weight(1f),
                    title = "Total emas",
                    value = totalGoldItems
                )
            }
        }

        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                SummaryCard(
                    modifier = Modifier.weight(1f),
                    title = "Harga dollar terbaru",
                    value = latestDollarPrice
                )
                SummaryCard(
                    modifier = Modifier.weight(1f),
                    title = "Harga emas / gram",
                    value = latestGoldPricePerGramIdr
                )
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = "5 daftar emas terbaru",
                        style = MaterialTheme.typography.titleMedium
                    )

                    when {
                        isLoading -> {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                CircularProgressIndicator()
                                Text(text = "Memuat dashboard emas...")
                            }
                        }

                        !errorMessage.isNullOrBlank() -> {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
                                Button(onClick = onRetry) { Text("Coba lagi") }
                            }
                        }

                        recentGolds.isEmpty() -> {
                            Text(
                                text = "Belum ada data emas yang bisa ditampilkan.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        else -> {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                recentGolds.take(5).forEachIndexed { index, gold ->
                                    GoldRow(gold = gold)
                                    if (index < recentGolds.take(5).lastIndex) {
                                        HorizontalDivider()
                                    }
                                }
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
    Card(modifier = modifier) {
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
    gold: Gold,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = gold.notes.ifBlank { "Emas #${gold.id}" }, style = MaterialTheme.typography.titleSmall)
            Text(
                text = listOfNotNull(
                    gold.type.replace('_', ' '),
                    formatDate(gold.date),
                ).joinToString(" · "),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "Jual: ${formatMoney(gold.sellPrice)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
            Text(
                text = "${formatWeight(gold.grams)} · ${formatMoney(gold.price)}",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = "Profit: ${formatSignedMoney(gold.profit)}",
                style = MaterialTheme.typography.bodySmall,
                color = if (gold.profit >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
            )
        }
    }
}

@Preview(showBackground = true, name = "Dashboard Emas - Default")
@Composable
private fun HomeGoldDashboardPreviewDefault() {
    ArtaTheme {
        Content(
            activeWalletName = "Tabungan Emas",
            totalAsset = "Rp 35.250.000",
            buyPrice = "Rp 30.000.000",
            profit = "+Rp 5.250.000",
            totalWeight = "15.00 g",
            totalGoldItems = "5 item",
            latestDollarPrice = "Rp 16.200",
            latestGoldPricePerGramIdr = "Rp 2.350.000",
            recentGolds = listOf(
                Gold(id = 1, walletId = 1, date = "2026-05-16T00:00:00Z", grams = 3.0, price = 4800000.0, type = "pure_gold", carat = 24.0, notes = "Emas 24K", createdAt = "", updatedAt = ""),
                Gold(id = 2, walletId = 1, date = "2026-05-15T00:00:00Z", grams = 2.5, price = 4000000.0, type = "pure_gold", carat = 24.0, notes = "Emas 24K", createdAt = "", updatedAt = ""),
                Gold(id = 3, walletId = 1, date = "2026-05-13T00:00:00Z", grams = 1.75, price = 3200000.0, type = "jewelry", carat = 18.0, notes = "Cincin emas", createdAt = "", updatedAt = ""),
                Gold(id = 4, walletId = 1, date = "2026-05-10T00:00:00Z", grams = 5.0, price = 7500000.0, type = "pure_gold", carat = 24.0, notes = "Emas 24K", createdAt = "", updatedAt = ""),
                Gold(id = 5, walletId = 1, date = "2026-05-08T00:00:00Z", grams = 2.75, price = 4500000.0, type = "jewelry", carat = 18.0, notes = "Kalung emas", createdAt = "", updatedAt = ""),
            ),
            isLoading = false,
            errorMessage = null,
        )
    }
}

@Preview(showBackground = true, name = "Dashboard Emas - Loading")
@Composable
private fun HomeGoldDashboardPreviewLoading() {
    ArtaTheme {
        Content(
            activeWalletName = "Tabungan Emas",
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
        )
    }
}

@Preview(showBackground = true, name = "Dashboard Emas - Error")
@Composable
private fun HomeGoldDashboardPreviewError() {
    ArtaTheme {
        Content(
            activeWalletName = "Tabungan Emas",
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

private fun formatSignedMoney(value: Double): String {
    val sign = if (value >= 0) "+" else "-"
    return "$sign${formatMoney(kotlin.math.abs(value))}"
}

private fun formatDate(value: String): String {
    if (value.isBlank()) return "-"

    val formatted = runCatching { OffsetDateTime.parse(value) }.getOrNull()?.toLocalDate()
        ?: runCatching { LocalDateTime.parse(value).toLocalDate() }.getOrNull()
        ?: runCatching { LocalDate.parse(value) }.getOrNull()

    return formatted?.toString() ?: value
}
