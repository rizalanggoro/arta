package id.my.rizalanggoro.arta.feature.home.presentation.dashboard.gold

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import id.my.rizalanggoro.arta.core.application.route.GoldRoute
import id.my.rizalanggoro.arta.core.extension.toAmericanCurrency
import id.my.rizalanggoro.arta.core.extension.toFormattedDate
import id.my.rizalanggoro.arta.core.extension.toIndonesianCurrency
import id.my.rizalanggoro.arta.core.utils.LocalBackStack
import id.my.rizalanggoro.arta.feature.home.presentation.dashboard.gold.component.LatestGold
import id.my.rizalanggoro.arta.feature.home.presentation.dashboard.gold.component.PriceSummary
import id.my.rizalanggoro.arta.openapi.models.DomainGold
import id.my.rizalanggoro.arta.openapi.models.DomainWallet
import id.my.rizalanggoro.arta.shared.component.ArtaMiuixTheme
import id.my.rizalanggoro.arta.shared.component.ErrorPlaceholder
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.InfiniteProgressIndicator
import top.yukonga.miuix.kmp.basic.PullToRefresh
import top.yukonga.miuix.kmp.basic.PullToRefreshState
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.rememberPullToRefreshState
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.All
import top.yukonga.miuix.kmp.icon.extended.Layers
import top.yukonga.miuix.kmp.icon.extended.Update
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.PressFeedbackType

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
        onClickManageTax = { backStack.add(GoldRoute.ListTax) },
        onClickPriceHistory = { type ->
            backStack.add(
                GoldRoute.PriceHistory(type = type)
            )
        },
        onClickEdit = { backStack.add(GoldRoute.Upsert(goldId = it.id)) },
        onClickDelete = { backStack.add(GoldRoute.Delete(goldId = it.id)) },
        onClickItem = { backStack.add(GoldRoute.Detail(id = it.id)) },
    )
}

@Composable
private fun Content(
    refreshState: PullToRefreshState = rememberPullToRefreshState(),
    uiState: GoldDashboardUiState = GoldDashboardUiState(),
    onClickRetry: () -> Unit = {},
    onClickManageTax: () -> Unit = {},
    onClickPriceHistory: (GoldRoute.PriceHistoryType) -> Unit = {},
    onRefresh: () -> Unit = {},
    onClickEdit: (DomainGold) -> Unit = {},
    onClickDelete: (DomainGold) -> Unit = {},
    onClickItem: (DomainGold) -> Unit = {},
) {
    when {
        uiState.isLoading -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            InfiniteProgressIndicator(color = MiuixTheme.colorScheme.primary)
        }

        uiState.errorMessage != null -> ErrorPlaceholder(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            onClickRetry = onClickRetry
        )

        else -> with(uiState) {
            PullToRefresh(
                pullToRefreshState = refreshState,
                isRefreshing = uiState.isRefreshing,
                onRefresh = onRefresh,
                modifier = Modifier.fillMaxSize()
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 96.dp,
                    ),
                ) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "Total asset",
                                    style = MiuixTheme.textStyles.footnote1,
                                    color = MiuixTheme.colorScheme.primary
                                )
                                Text(
                                    text = (data?.totalAsset ?: 0.0).toIndonesianCurrency(),
                                    style = MiuixTheme.textStyles.title2.copy(
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                                Text(
                                    text = "Harga beli ${
                                        (data?.totalBuyPrice ?: 0.0)
                                            .toIndonesianCurrency()
                                    }",
                                    style = MiuixTheme.textStyles.footnote2,
                                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Rounded.TrendingUp,
                                        null,
                                        modifier = Modifier.size(16.dp),
                                        tint = MiuixTheme.colorScheme.onSurfaceVariantSummary
                                    )
                                    Text(
                                        text = (data?.profit ?: 0.0)
                                            .toIndonesianCurrency(),
                                        style = MiuixTheme.textStyles.footnote2,
                                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary
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
                                .padding(top = 12.dp)
                        ) {
                            listOf(
                                Triple(
                                    "Total berat",
                                    "${(data?.totalWeight?.toFloat() ?: 0)} gr",
                                    MiuixIcons.Layers
                                ),
                                Triple(
                                    "Total emas",
                                    (data?.totalGoldItems ?: 0).toString(),
                                    MiuixIcons.All
                                ),
                            ).forEach { (title, value, icon) ->
                                Card(
                                    colors = CardDefaults.defaultColors(
                                        color = MiuixTheme.colorScheme.secondaryContainer
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(icon, null)
                                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                            Text(
                                                text = value,
                                                style = MiuixTheme.textStyles.body1.copy(
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                color = MiuixTheme.colorScheme.onBackground,
                                            )
                                            Text(
                                                text = title,
                                                style = MiuixTheme.textStyles.footnote1,
                                                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
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
                                .padding(top = 12.dp)
                        ) {
                            listOf(
                                Triple(
                                    "Emas dunia",
                                    (data?.goldPrice?.pricePerOunceUsd
                                        ?: 0.0).toAmericanCurrency(),
                                    GoldRoute.PriceHistoryType.GOLD
                                ),
                                Triple(
                                    "Nilai dollar",
                                    (data?.fxRate?.rate ?: 0).toIndonesianCurrency(),
                                    GoldRoute.PriceHistoryType.FX
                                )
                            ).forEach { (title, value, type) ->
                                Card(
                                    onClick = { onClickPriceHistory(type) },
                                    pressFeedbackType = PressFeedbackType.Tilt,
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.defaultColors(
                                        color = MiuixTheme.colorScheme.secondaryContainer
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        Text(
                                            text = title,
                                            style = MiuixTheme.textStyles.footnote1,
                                            color = MiuixTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = value,
                                            style = MiuixTheme.textStyles.body1.copy(
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = MiuixTheme.colorScheme.onBackground
                                        )
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                MiuixIcons.Update,
                                                null,
                                                modifier = Modifier.size(12.dp),
                                                tint = MiuixTheme.colorScheme.onSurfaceVariantSummary
                                            )
                                            Text(
                                                text = when (type) {
                                                    GoldRoute.PriceHistoryType.GOLD ->
                                                        data?.goldPrice?.createdAt

                                                    else -> data?.fxRate?.createdAt
                                                }.toFormattedDate("dd/MM/yyyy HH:mm"),
                                                style = MiuixTheme.textStyles.footnote2,
                                                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
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
                            onClickItem = onClickItem,
                            onClickEdit = onClickEdit,
                            onClickDelete = onClickDelete,
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
    ArtaMiuixTheme {
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
    ArtaMiuixTheme {
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
    ArtaMiuixTheme {
        Content(
            uiState = GoldDashboardUiState(
                isLoading = false,
                errorMessage = "Gagal memuat dashboard emas.",
            ),
        )
    }
}
