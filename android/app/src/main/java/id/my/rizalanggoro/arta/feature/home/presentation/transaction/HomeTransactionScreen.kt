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
import id.my.rizalanggoro.arta.core.application.route.TransactionRoute
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.core.utils.LocalBackStack
import id.my.rizalanggoro.arta.openapi.models.DomainCategory
import id.my.rizalanggoro.arta.openapi.models.DomainTransaction
import id.my.rizalanggoro.arta.openapi.models.DtoTransaction
import id.my.rizalanggoro.arta.shared.component.TransactionListItem
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme
import kotlinx.coroutines.flow.filterIsInstance
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Composable
fun HomeTransactionScreen(vm: TransactionListVM = hiltViewModel()) {
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
        onClickRetry = vm::loadTransactions,
        onRefresh = { vm.loadTransactions(isRefresh = true) },
        onLongClickItem = {
            backStack.add(
                TransactionRoute.ActionSheet(
                    transactionId = it.id
                )
            )
        }
    )
}

@Composable
private fun Content(
    pullToRefreshState: PullToRefreshState = PullToRefreshState(),
    uiState: TransactionListUiState = TransactionListUiState(),
    onClickRetry: () -> Unit = {},
    onRefresh: () -> Unit = {},
    onLongClickItem: (DomainTransaction) -> Unit = {},
    onClickNext: () -> Unit = {},
    onClickPrevious: () -> Unit = {},
) {
    with(uiState) {
        PullToRefreshBox(
            state = pullToRefreshState,
            isRefreshing = uiState.isRefreshing,
            onRefresh = onRefresh,
            indicator = {
                PullToRefreshDefaults.LoadingIndicator(
                    state = pullToRefreshState,
                    isRefreshing = uiState.isRefreshing,
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
                        FilledTonalIconButton(onClick = onClickPrevious) {
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
                                "Tahunan",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                "2026",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                        FilledTonalIconButton(onClick = onClickNext) {
                            Icon(
                                Icons.Rounded.ChevronRight,
                                null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }

                itemsIndexed(uiState.transactions) { index, transaction ->
                    TransactionListItem(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(
                                top = when {
                                    index == 0 -> 16.dp
                                    else -> 2.dp
                                }
                            ),
                        transaction = transaction.data,
                        category = transaction.category,
                        index = index,
                        size = uiState.transactions.size,
                        onLongClick = {
                            onLongClickItem(transaction.data)
                        }
                    )
                }
                item {
                    Text(
                        "Tekan dan tahan untuk melihat opsi lainnya",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
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
            uiState = TransactionListUiState(
                transactions = List(5) {
                    DtoTransaction(
                        data = DomainTransaction(
                            amount = (it + 1) * 10000.0,
                            categoryId = 1,
                            createdAt = OffsetDateTime.now()
                                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                            date = OffsetDateTime.now()
                                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                            description = "",
                            id = it + 1,
                            updatedAt = OffsetDateTime.now()
                                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                            walletId = it + 1
                        ),
                        category = DomainCategory(
                            createdAt = OffsetDateTime.now()
                                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                            id = it + 1,
                            name = "Makanan dan minuman",
                            type = "expense",
                            updatedAt = OffsetDateTime.now()
                                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                            userId = 1
                        )
                    )
                }
            )
        )
    }
}
