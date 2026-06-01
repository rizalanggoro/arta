package id.my.rizalanggoro.arta.feature.home.presentation.transaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.LoadingIndicator
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import id.my.rizalanggoro.arta.R
import id.my.rizalanggoro.arta.core.utils.LocalBackStack
import id.my.rizalanggoro.arta.core.application.Routes
import id.my.rizalanggoro.arta.core.application.Routes.TransactionUpsertRoute
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.openapi.models.DomainCategory
import id.my.rizalanggoro.arta.openapi.models.DomainTransaction
import id.my.rizalanggoro.arta.openapi.models.DtoTransaction
import id.my.rizalanggoro.arta.shared.component.ConfirmDialog
import id.my.rizalanggoro.arta.shared.component.EmptyPlaceholder
import id.my.rizalanggoro.arta.shared.component.ErrorPlaceholder
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
                    TransactionUpsertRoute(
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
                Routes.TransactionActionSheetRoute(
                    transactionId = it.id
                )
            )
        }
    )

    if (uiState.targetDeleteTransactionId != null)
        ConfirmDialog(
            title = "Hapus",
            description = "Apakah Anda yakin akan menghapus transaksi yang dipilih? " +
                    "Tindakan ini tidak dapat dipulihkan",
            onDismissRequest = vm::onDeleteTransactionDialogDismissed,
            onConfirmRequest = vm::onDeleteTransactionDialogClicked,
            isLoading = uiState.isDeleting,
            confirmText = "Hapus"
        )
}

@Composable
private fun Content(
    pullToRefreshState: PullToRefreshState = PullToRefreshState(),
    uiState: TransactionListUiState = TransactionListUiState(),
    onClickRetry: () -> Unit = {},
    onRefresh: () -> Unit = {},
    onLongClickItem: (DomainTransaction) -> Unit = {},
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
            message = uiState.errorMessage,
            onClickRetry = onClickRetry
        )

        uiState.transactions.isEmpty() -> EmptyPlaceholder(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        )

        else -> PullToRefreshBox(
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                itemsIndexed(uiState.transactions) { index, transaction ->
                    TransactionListItem(
                        transaction = transaction,
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

@Preview(showBackground = true)
@Composable
private fun LoadingPreview() {
    ArtaTheme {
        Content(
            uiState = TransactionListUiState(
                isLoading = true
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyPreview() {
    ArtaTheme {
        Content(
            uiState = TransactionListUiState(
                transactions = emptyList()
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorPreview() {
    ArtaTheme {
        Content(
            uiState = TransactionListUiState(
                errorMessage = stringResource(R.string.client_error)
            )
        )
    }
}
