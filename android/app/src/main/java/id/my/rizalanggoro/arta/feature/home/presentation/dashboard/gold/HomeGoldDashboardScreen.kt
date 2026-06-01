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
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import id.my.rizalanggoro.arta.core.application.Routes
import id.my.rizalanggoro.arta.core.extension.toAmericanCurrency
import id.my.rizalanggoro.arta.core.extension.toFormattedDate
import id.my.rizalanggoro.arta.core.extension.toIndonesianCurrency
import id.my.rizalanggoro.arta.core.utils.LocalBackStack
import id.my.rizalanggoro.arta.feature.home.presentation.dashboard.gold.component.LatestGold
import id.my.rizalanggoro.arta.feature.home.presentation.dashboard.gold.component.PriceSummary
import id.my.rizalanggoro.arta.openapi.models.DomainWallet
import id.my.rizalanggoro.arta.shared.component.ErrorPlaceholder
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme

@Composable
fun HomeGoldDashboardScreen(
    vm: GoldDashboardVM = hiltViewModel(),
) {
    val backStack = LocalBackStack.current
    val uiState by vm.uiState.collectAsState()

    Content(
        uiState = uiState,
        onClickRetry = { vm.loadDashboard() },
        refreshState = rememberPullToRefreshState(),
        onRefresh = { vm.loadDashboard(isRefresh = true) },
        onClickManageTax = { backStack.add(Routes.GoldTaxListRoute) },
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
private fun Content(
    refreshState: PullToRefreshState = PullToRefreshState(),
    uiState: GoldDashboardUiState = GoldDashboardUiState(),
    onClickRetry: () -> Unit = {},
    onClickManageTax: () -> Unit = {},
    onRefresh: () -> Unit = {},
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

        else -> with(uiState) {
            PullToRefreshBox(
                state = refreshState,
                isRefreshing = uiState.isRefreshing,
                onRefresh = onRefresh,
                indicator = {
                    PullToRefreshDefaults.LoadingIndicator(
                        state = refreshState,
                        isRefreshing = uiState.isRefreshing,
                        modifier = Modifier.align(Alignment.TopCenter)
                    )
                }
            ) {
                LazyColumn(
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
                                text = (data?.totalAsset ?: 0.0).toIndonesianCurrency(),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Column {
                                Text(
                                    text = "Harga beli ${
                                        (data?.totalBuyPrice ?: 0.0)
                                            .toIndonesianCurrency()
                                    }",
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
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.outline
                                    )
                                    Text(
                                        text = (data?.profit ?: 0.0)
                                            .toIndonesianCurrency(),
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
                                    "value" to "${(data?.totalWeight?.toFloat() ?: 0)} gr",
                                    "icon" to Icons.Rounded.Balance
                                ),
                                mapOf(
                                    "title" to "Total emas",
                                    "value" to (data?.totalGoldItems ?: 0).toString(),
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
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer,
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
                                mapOf(
                                    "title" to "Emas dunia",
                                    "value" to (data?.goldPrice?.pricePerOunceUsd
                                        ?: 0.0).toAmericanCurrency(),
                                    "date" to data?.goldPrice?.createdAt
                                ),
                                mapOf(
                                    "title" to "Nilai dollar",
                                    "value" to (data?.fxRate?.rate ?: 0).toIndonesianCurrency(),
                                    "date" to data?.fxRate?.createdAt
                                )
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
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                        Text(
                                            text = it["value"] as String,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
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
                                                text = it["date"].toFormattedDate("dd/MM/yyyy HH:mm"),
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
                        PriceSummary(
                            onClickManageTax = onClickManageTax,
                            retailPrice = data?.retailPrice ?: 0.0,
                            goldTaxes = data?.goldTaxes ?: emptyList(),
                        )
                    }

                    item {
                        LatestGold(
                            golds = data?.latestGolds ?: emptyList(),
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
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
                isLoading = false,
                errorMessage = "Gagal memuat dashboard emas.",
            ),
        )
    }
}
