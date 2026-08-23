package id.my.rizalanggoro.arta.feature.home.presentation.dashboard.cash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.valentinilk.shimmer.shimmer
import id.my.rizalanggoro.arta.R
import id.my.rizalanggoro.arta.core.application.route.CategoryRoute
import id.my.rizalanggoro.arta.core.extension.toFormattedDate
import id.my.rizalanggoro.arta.core.extension.toIndonesianCurrency
import id.my.rizalanggoro.arta.core.extension.toIndonesianDate
import id.my.rizalanggoro.arta.core.utils.LocalBackStack
import id.my.rizalanggoro.arta.core.utils.Samples
import id.my.rizalanggoro.arta.feature.home.presentation.dashboard.cash.HomeCashDashboardUiState.TimeFilter
import id.my.rizalanggoro.arta.feature.home.presentation.dashboard.cash.component.IncomeExpenseSummary
import id.my.rizalanggoro.arta.openapi.models.DomainCategory
import id.my.rizalanggoro.arta.shared.component.ArtaMiuixTheme
import id.my.rizalanggoro.arta.shared.component.DashboardCategoryListItem
import id.my.rizalanggoro.arta.shared.component.EmptyPlaceholder
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.PullToRefresh
import top.yukonga.miuix.kmp.basic.PullToRefreshState
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.TabRowWithContour
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.rememberPullToRefreshState
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Hide
import top.yukonga.miuix.kmp.icon.extended.Show
import top.yukonga.miuix.kmp.theme.MiuixTheme

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
private fun Content(
    uiState: HomeCashDashboardUiState = HomeCashDashboardUiState(),
    pullToRefreshState: PullToRefreshState = rememberPullToRefreshState(),
    onChangeTimeFilter: (TimeFilter) -> Unit = {},
    onRefresh: () -> Unit = {},
    onClickBalanceVisibility: (Boolean) -> Unit = {},
    onClickCategory: (DomainCategory) -> Unit = {},
) {
    with(uiState) {
        PullToRefresh(
            modifier = Modifier.fillMaxSize(),
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            pullToRefreshState = pullToRefreshState,
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
                    Card(modifier = Modifier.padding(top = 16.dp)) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Saldo saat ini",
                                style = MiuixTheme.textStyles.footnote1,
                                color = when {
                                    isLoading -> Color.Transparent
                                    else -> MiuixTheme.colorScheme.onSurfaceVariantSummary
                                },
                                modifier = Modifier.then(
                                    when {
                                        isLoading -> Modifier
                                            .shimmer()
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(MiuixTheme.colorScheme.outline)

                                        else -> Modifier
                                    }
                                )
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
                                    style = MiuixTheme.textStyles.title2.copy(fontWeight = FontWeight.Bold),
                                    color = when {
                                        isLoading -> Color.Transparent
                                        else -> Color.Unspecified
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .then(
                                            when {
                                                isLoading -> Modifier
                                                    .shimmer()
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(MiuixTheme.colorScheme.outline)

                                                else -> Modifier
                                            }
                                        )
                                )
                                IconButton(
                                    onClick = { onClickBalanceVisibility(!isBalanceVisible) },
                                    backgroundColor = when {
                                        isLoading -> MiuixTheme.colorScheme.outline
                                        else -> Color.Unspecified
                                    },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .then(
                                            when {
                                                isLoading -> Modifier.shimmer()
                                                else -> Modifier
                                            }
                                        )
                                ) {
                                    if (!isLoading)
                                        Icon(
                                            when {
                                                uiState.isBalanceVisible -> MiuixIcons.Hide
                                                else -> MiuixIcons.Show
                                            },
                                            null,
                                            tint = MiuixTheme.colorScheme.primary
                                        )
                                }
                            }
                        }
                    }
                }

                item {
                    Card(modifier = Modifier.padding(top = 12.dp)) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            TabRowWithContour(
                                tabs = listOf("Hari ini", "Minggu ini", "Bulan ini"),
                                selectedTabIndex = when (uiState.timeFilter) {
                                    TimeFilter.Today -> 0
                                    TimeFilter.ThisWeek -> 1
                                    TimeFilter.ThisMonth -> 2
                                },
                                onTabSelected = { index ->
                                    onChangeTimeFilter(
                                        when (index) {
                                            0 -> TimeFilter.Today
                                            1 -> TimeFilter.ThisWeek
                                            else -> TimeFilter.ThisMonth
                                        }
                                    )
                                }
                            )
                            Column(
                                verticalArrangement = Arrangement.spacedBy(
                                    when {
                                        isLoading -> 2.dp
                                        else -> 0.dp
                                    }
                                )
                            ) {
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
                                    style = MiuixTheme.textStyles.body2,
                                    color = when {
                                        isLoading -> Color.Transparent
                                        else -> MiuixTheme.colorScheme.onSurfaceVariantSummary
                                    },
                                    maxLines = when {
                                        isLoading -> 1
                                        else -> Int.MAX_VALUE
                                    },
                                    modifier = Modifier.then(
                                        when {
                                            isLoading -> Modifier
                                                .shimmer()
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(MiuixTheme.colorScheme.outline)

                                            else -> Modifier
                                        }
                                    )
                                )
                                if (isLoading)
                                    Text(
                                        "loading",
                                        style = MiuixTheme.textStyles.body2,
                                        color = Color.Transparent,
                                        modifier = Modifier
                                            .fillMaxWidth(.5f)
                                            .shimmer()
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(MiuixTheme.colorScheme.outline)
                                    )
                            }

                            IncomeExpenseSummary(
                                totalIncome = uiState.data?.totalIncome ?: 0.0,
                                totalExpense = uiState.data?.totalExpense ?: 0.0,
                                prevPeriodIncome = uiState.data?.prevPeriodIncome ?: 0.0,
                                prevPeriodExpense = uiState.data?.prevPeriodExpense ?: 0.0,
                                isLoading = isLoading
                            )
                        }
                    }
                }

                item {
                    SmallTitle(
                        text = "Transaksi terbaru",
                        modifier = Modifier.padding(top = 16.dp),
                        insideMargin = PaddingValues(top = 8.dp),
                    )
                }

                if ((uiState.data?.latestCategories?.isEmpty() ?: true) && !uiState.isLoading) {
                    item {
                        EmptyPlaceholder(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp)
                        )
                    }
                }

                if (isLoading) {
                    items(3) {
                        DashboardCategoryListItem(
                            index = it,
                            size = 3,
                            modifier = Modifier.padding(
                                top = when {
                                    it == 0 -> 16.dp
                                    else -> 2.dp
                                }
                            ),
                            category = Samples.dtoCategories[it],
                            isLoading = true
                        )
                    }
                }

                itemsIndexed(uiState.data?.latestCategories ?: emptyList()) { index, category ->
                    DashboardCategoryListItem(
                        modifier = Modifier.padding(
                            top = when {
                                index == 0 -> 16.dp
                                else -> 2.dp
                            },
                        ),
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
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    ArtaMiuixTheme {
        Content()
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadingPreview() {
    ArtaMiuixTheme {
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
    ArtaMiuixTheme {
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
    ArtaMiuixTheme {
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
    ArtaMiuixTheme {
        Content()
    }
}
