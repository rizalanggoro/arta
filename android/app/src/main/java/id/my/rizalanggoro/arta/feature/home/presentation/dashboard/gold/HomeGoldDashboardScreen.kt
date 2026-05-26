package id.my.rizalanggoro.arta.feature.home.presentation.dashboard.gold

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.Balance
import androidx.compose.material.icons.rounded.Tag
import androidx.compose.material.icons.rounded.Update
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.feature.home.presentation.dashboard.gold.component.LatestGold
import id.my.rizalanggoro.arta.feature.home.presentation.dashboard.gold.component.PriceSummary
import id.my.rizalanggoro.arta.openapi.models.DomainGold
import id.my.rizalanggoro.arta.openapi.models.DomainWallet
import id.my.rizalanggoro.arta.openapi.models.DtoGold
import id.my.rizalanggoro.arta.shared.component.ErrorPlaceholder
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme
import kotlinx.coroutines.flow.filterIsInstance
import java.math.BigDecimal

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
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
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
                        style = MaterialTheme.typography.bodySmall,
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
                    listOf(
                        mapOf(
                            "title" to "Total berat",
                            "value" to uiState.totalWeight,
                            "icon" to Icons.Rounded.Balance
                        ),
                        mapOf(
                            "title" to "Total emas",
                            "value" to uiState.totalGoldItems,
                            "icon" to Icons.Rounded.Tag
                        )
                    ).forEach {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    it["icon"] as ImageVector,
                                    null
                                )
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        text = it["value"] as String,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = it["title"] as String,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.outline,
                                    )
                                }
                            }
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
                        .padding(top = 8.dp)
                ) {
                    listOf(
                        mapOf("title" to "Emas dunia", "value" to "$4000"),
                        mapOf("title" to "Nilai dollar", "value" to uiState.latestDollarPrice)
                    ).forEach {
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = it["title"] as String,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Normal
                                )
                                Text(
                                    text = it["value"] as String,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        Icons.Rounded.Update,
                                        null,
                                        modifier = Modifier.size(12.dp),
                                        tint = MaterialTheme.colorScheme.outline
                                    )
                                    Text(
                                        text = "25/05/26 07.19",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.outline,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                PriceSummary()
            }

            item {
                LatestGold(
                    golds = uiState.recentGolds,
                    onClickShowMore = {},
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    wallpaper = Wallpapers.GREEN_DOMINATED_EXAMPLE, device = "id:pixel_9_pro_xl"
)
@Composable
private fun Preview() {
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
