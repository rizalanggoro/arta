package id.my.rizalanggoro.arta.feature.home.presentation.transaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import id.my.rizalanggoro.arta.core.application.route.CategoryRoute
import id.my.rizalanggoro.arta.core.application.route.TransactionRoute
import id.my.rizalanggoro.arta.core.constant.TransactionGroupType
import id.my.rizalanggoro.arta.core.constant.TransactionTimeRangeType
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.core.extension.toFormattedDate
import id.my.rizalanggoro.arta.core.extension.toIndonesianDate
import id.my.rizalanggoro.arta.core.utils.LocalBackStack
import id.my.rizalanggoro.arta.core.utils.Samples
import id.my.rizalanggoro.arta.openapi.models.DomainCategory
import id.my.rizalanggoro.arta.openapi.models.DomainTransaction
import id.my.rizalanggoro.arta.shared.component.DashboardCategoryListItem
import id.my.rizalanggoro.arta.shared.component.EmptyPlaceholder
import id.my.rizalanggoro.arta.shared.component.TransactionListItem
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme
import kotlinx.coroutines.flow.filterIsInstance

@Composable
fun HomeTransactionScreen(vm: HomeTransactionVM = hiltViewModel()) {
    val backStack = LocalBackStack.current
    val uiState by vm.uiState.collectAsState()

    LaunchedEffect(Unit) {
        AppEventBus.event
            .filterIsInstance<AppEvent.TransactionActionSheet.OnEditClicked>()
            .collect {
                backStack.add(
                    TransactionRoute.Upsert(
                        transactionId = it.transactionId
                    )
                )
            }
    }

    Content(
        uiState = uiState,
        onRefresh = vm::loadTransactions,
        onClickCategory = {
            backStack.add(
                CategoryRoute.Detail(
                    categoryId = it.id,
                    transactionStartDateMillis = uiState.startDateMillis,
                    transactionEndDateMillis = uiState.endDateMillis
                )
            )
        },
        onClickItem = {
            backStack.add(
                TransactionRoute.Detail(transactionId = it.id)
            )
        },
        onLongClickItem = {
            backStack.add(
                TransactionRoute.ActionSheet(transactionId = it.id)
            )
        },
        onClickNextTimeRange = vm::onNextTimeRangeClicked,
        onClickPrevTimeRange = vm::onPrevTimeRangeClicked
    )
}

@Composable
private fun Content(
    pullToRefreshState: PullToRefreshState = PullToRefreshState(),
    uiState: HomeTransactionUiState = HomeTransactionUiState(),
    onRefresh: () -> Unit = {},
    onClickCategory: (DomainCategory) -> Unit = {},
    onClickItem: (DomainTransaction) -> Unit = {},
    onLongClickItem: (DomainTransaction) -> Unit = {},
    onClickNextTimeRange: () -> Unit = {},
    onClickPrevTimeRange: () -> Unit = {},
) {
    with(uiState) {
        val hasData = when (groupBy) {
            TransactionGroupType.CATEGORY -> categories.isNotEmpty()
            TransactionGroupType.TRANSACTION -> transactions.isNotEmpty()
        }

        PullToRefreshBox(
            state = pullToRefreshState,
            isRefreshing = uiState.isLoading && hasData,
            onRefresh = onRefresh,
            indicator = {
                PullToRefreshDefaults.LoadingIndicator(
                    state = pullToRefreshState,
                    isRefreshing = uiState.isLoading && hasData,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                stickyHeader {
                    Row(
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        FilledTonalIconButton(onClick = onClickPrevTimeRange) {
                            Icon(
                                Icons.Rounded.ChevronLeft,
                                null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                when (timeRange) {
                                    TransactionTimeRangeType.DAILY -> "Harian"
                                    TransactionTimeRangeType.WEEKLY -> "Mingguan"
                                    TransactionTimeRangeType.MONTHLY -> "Bulanan"
                                },
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                when (timeRange) {
                                    TransactionTimeRangeType.DAILY -> startDateMillis.toIndonesianDate()
                                    TransactionTimeRangeType.WEEKLY -> {
                                        val endMillis = endDateMillis - 86400000
                                        val startMonth = startDateMillis.toFormattedDate("MMMM yyyy")
                                        val endMonth = endMillis.toFormattedDate("MMMM yyyy")
                                        if (startMonth == endMonth) {
                                            "${startDateMillis.toFormattedDate("d")} - ${endMillis.toFormattedDate("d MMMM yyyy")}"
                                        } else {
                                            "${startDateMillis.toFormattedDate("d MMMM")} - ${endMillis.toFormattedDate("d MMMM yyyy")}"
                                        }
                                    }
                                    TransactionTimeRangeType.MONTHLY -> startDateMillis.toFormattedDate("MMMM yyyy")
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                        FilledTonalIconButton(onClick = onClickNextTimeRange) {
                            Icon(
                                Icons.Rounded.ChevronRight,
                                null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }

                if (isLoading) {
                    val skeletonSize = when (groupBy) {
                        TransactionGroupType.CATEGORY -> 3
                        TransactionGroupType.TRANSACTION -> 5
                    }
                    items(skeletonSize) {
                        when (groupBy) {
                            TransactionGroupType.CATEGORY -> {
                                DashboardCategoryListItem(
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp)
                                        .padding(top = if (it == 0) 16.dp else 2.dp),
                                    category = Samples.dtoCategories[it],
                                    isLoading = true
                                )
                            }
                            TransactionGroupType.TRANSACTION -> {
                                TransactionListItem(
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp)
                                        .padding(top = if (it == 0) 16.dp else 2.dp),
                                    index = it,
                                    size = skeletonSize,
                                    transaction = Samples.domainTransactions.first(),
                                    category = Samples.domainCategories.first(),
                                    isLoading = true
                                )
                            }
                        }
                    }
                }

                if (!isLoading && !hasData) {
                    item {
                        EmptyPlaceholder(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 32.dp)
                        )
                    }
                }

                when (groupBy) {
                    TransactionGroupType.CATEGORY -> {
                        itemsIndexed(uiState.categories) { index, category ->
                            DashboardCategoryListItem(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .padding(top = if (index == 0) 16.dp else 2.dp),
                                category = category,
                                index = index,
                                size = uiState.categories.size,
                                onClick = onClickCategory,
                            )
                        }

                        if (!isLoading && categories.isNotEmpty()) {
                            item {
                                FooterText("Tekan untuk melihat transaksi")
                            }
                        }
                    }

                    TransactionGroupType.TRANSACTION -> {
                        itemsIndexed(uiState.transactions) { index, transaction ->
                            TransactionListItem(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .padding(top = if (index == 0) 16.dp else 2.dp),
                                transaction = transaction.data,
                                category = transaction.category!!,
                                index = index,
                                size = uiState.transactions.size,
                                onClick = onClickItem,
                                onLongClick = { onLongClickItem(transaction.data) }
                            )
                        }

                        if (!isLoading && transactions.isNotEmpty()) {
                            item {
                                FooterText("Tekan dan tahan untuk melihat opsi lainnya")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FooterText(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.outline,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    ArtaTheme {
        Content(
            uiState = HomeTransactionUiState(
                categories = Samples.dtoCategories.take(3)
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TransactionPreview() {
    ArtaTheme {
        Content(
            uiState = HomeTransactionUiState(
                groupBy = TransactionGroupType.TRANSACTION
            )
        )
    }
}
