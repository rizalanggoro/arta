package id.my.rizalanggoro.arta.feature.home.presentation.dashboard.cash

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import id.my.rizalanggoro.arta.R
import id.my.rizalanggoro.arta.core.application.route.CategoryRoute
import id.my.rizalanggoro.arta.core.extension.toFormattedDate
import id.my.rizalanggoro.arta.core.extension.toIndonesianCurrency
import id.my.rizalanggoro.arta.core.extension.toIndonesianDate
import id.my.rizalanggoro.arta.core.utils.LocalBackStack
import id.my.rizalanggoro.arta.feature.home.presentation.dashboard.cash.HomeCashDashboardUiState.TimeFilter
import id.my.rizalanggoro.arta.feature.home.presentation.dashboard.cash.component.IncomeExpenseSummary
import id.my.rizalanggoro.arta.openapi.models.DomainCategory
import id.my.rizalanggoro.arta.shared.component.DashboardCategoryListItem
import id.my.rizalanggoro.arta.shared.component.EmptyPlaceholder
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme

@Composable
fun HomeCashDashboardScreen(vm: HomeCashDashboardVM = hiltViewModel()) {
    val backStack = LocalBackStack.current
    val uiState by vm.uiState.collectAsState()
    val pullToRefreshState = rememberPullToRefreshState()

    Content(
        uiState = uiState,
        pullToRefreshState = pullToRefreshState,
        onChangeTimeFilter = vm::timeFilterChanged,
        onRefresh = { vm.loadDashboard(isRefresh = true) },
        onClickBalanceVisibility = vm::onBalanceVisibilityChanged,
        onClickCategory = {
            backStack.add(
                CategoryRoute.Detail(
                    categoryId = it.id,
                    transactionStartDateMillis = uiState.startDateMillis,
                    transactionEndDateMillis = uiState.endDateMillis
                )
            )
        }
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
private fun Content(
    uiState: HomeCashDashboardUiState = HomeCashDashboardUiState(),
    pullToRefreshState: PullToRefreshState = rememberPullToRefreshState(),
    onChangeTimeFilter: (TimeFilter) -> Unit = {},
    onRefresh: () -> Unit = {},
    onClickBalanceVisibility: (Boolean) -> Unit = {},
    onClickCategory: (DomainCategory) -> Unit = {},
) {
    PullToRefreshBox(
        modifier = Modifier.fillMaxSize(),
        isRefreshing = uiState.isRefreshing,
        onRefresh = onRefresh,
        state = pullToRefreshState,
        indicator = {
            PullToRefreshDefaults.LoadingIndicator(
                state = pullToRefreshState,
                isRefreshing = uiState.isRefreshing,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    ) {
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp),
                ) {
                    Text(
                        text = "Saldo saat ini",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = when {
                                uiState.isBalanceVisible -> (uiState.data?.currentBalance
                                    ?: 0.0).toIndonesianCurrency()

                                else -> "•".repeat(8)
                            },
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        IconButton(
                            onClick = {
                                onClickBalanceVisibility(!uiState.isBalanceVisible)
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                when {
                                    uiState.isBalanceVisible -> Icons.Rounded.VisibilityOff
                                    else -> Icons.Rounded.Visibility
                                },
                                null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        listOf(
                            mapOf(
                                "title" to "Hari ini",
                                "filter" to TimeFilter.Today
                            ),
                            mapOf(
                                "title" to "Minggu ini",
                                "filter" to TimeFilter.ThisWeek
                            ),
                            mapOf(
                                "title" to "Bulan ini",
                                "filter" to TimeFilter.ThisMonth
                            ),
                        ).forEachIndexed { index, item ->
                            ToggleButton(
                                colors = ToggleButtonDefaults.toggleButtonColors(
                                    containerColor = MaterialTheme.colorScheme.background
                                ),
                                checked = item["filter"] == uiState.timeFilter,
                                onCheckedChange = {
                                    onChangeTimeFilter(item["filter"] as TimeFilter)
                                },
                                shapes = when (index) {
                                    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                                    2 -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                                    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                                },
                                enabled = !uiState.isRefreshing
                            ) {
                                Text(item["title"] as String)
                            }
                        }
                    }
                    Text(
                        "Berikut ringkasan pemasukan, pengeluaran, dan transaksi terbaru Anda " +
                                when (uiState.timeFilter) {
                                    TimeFilter.Today -> "pada ${uiState.startDateMillis.toIndonesianDate()}"

                                    TimeFilter.ThisWeek -> "selama satu minggu mulai " +
                                            "${uiState.startDateMillis.toIndonesianDate()} " +
                                            "hingga ${uiState.endDateStr}"

                                    TimeFilter.ThisMonth -> "selama bulan " +
                                            uiState.startDateMillis.toFormattedDate("MMMM yyyy")
                                },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            item {
                IncomeExpenseSummary(
                    totalIncome = uiState.data?.totalIncome ?: 0.0,
                    totalExpense = uiState.data?.totalExpense ?: 0.0,
                )
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .padding(top = 16.dp)
                ) {
                    Text(
                        "Transaksi terbaru",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(
                                RoundedCornerShape(
                                    topStart = 24.dp,
                                    topEnd = 24.dp
                                )
                            )
                            .background(MaterialTheme.colorScheme.background)
                            .padding(horizontal = 16.dp)
                            .padding(top = 16.dp)
                    )
                }
            }

            if ((uiState.data?.latestCategories?.isEmpty() ?: true) && !uiState.isLoading) {
                item {
                    EmptyPlaceholder(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 16.dp,
                                vertical = 32.dp
                            )
                    )
                }
            }

            if (uiState.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        LoadingIndicator()
                    }
                }
            }

            itemsIndexed(uiState.data?.latestCategories ?: emptyList()) { index, category ->
                DashboardCategoryListItem(
                    modifier = Modifier
                        .padding(
                            top = when {
                                index == 0 -> 16.dp
                                else -> 2.dp
                            },
                        )
                        .padding(horizontal = 16.dp),
                    category = category,
                    index = index,
                    size = uiState.data?.latestCategories?.size ?: 0,
                    onClick = onClickCategory,
                )
            }

            item {
                Box(modifier = Modifier.height((56 + 32).dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    ArtaTheme {
        Content()
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadingPreview() {
    ArtaTheme {
        Content(
            uiState = HomeCashDashboardUiState(
                isLoading = true
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RefreshingPreview() {
    ArtaTheme {
        Content(
            uiState = HomeCashDashboardUiState(
                isRefreshing = true
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorPreview() {
    ArtaTheme {
        Content(
            uiState = HomeCashDashboardUiState(
                errorMessage = stringResource(R.string.client_error)
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyPreview() {
    ArtaTheme {
        Content()
    }
}

