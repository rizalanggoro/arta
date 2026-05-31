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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
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
import id.my.rizalanggoro.arta.core.LocalBackStack
import id.my.rizalanggoro.arta.core.Routes
import id.my.rizalanggoro.arta.core.extension.toIndonesianCurrency
import id.my.rizalanggoro.arta.feature.home.presentation.dashboard.cash.component.IncomeExpenseSummary
import id.my.rizalanggoro.arta.openapi.models.DomainTransaction
import id.my.rizalanggoro.arta.shared.component.ErrorPlaceholder
import id.my.rizalanggoro.arta.shared.component.TransactionListItem
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme

@Composable
fun HomeCashDashboardScreen(vm: HomeCashDashboardVM = hiltViewModel()) {
    val backStack = LocalBackStack.current
    val uiState by vm.uiState.collectAsState()

    Content(
        uiState = uiState,
        onLongClickTransaction = {
            backStack.add(
                Routes.TransactionActionSheetRoute(
                    transactionId = it.id
                )
            )
        }
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
private fun Content(
    uiState: CashDashboardUiState = CashDashboardUiState(),
    onClickRetry: () -> Unit = {},
    onLongClickTransaction: (DomainTransaction) -> Unit = {},
) {
    when {
        uiState.isLoading -> Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center,
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

        else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp)
                ) {
                    Text(
                        text = "Saldo saat ini",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Text(
                        text = (uiState.data?.currentBalance ?: 0.0).toIndonesianCurrency(),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
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
                            "Hari ini",
                            "Minggu ini",
                            "Bulan ini"
                        ).forEachIndexed { index, label ->
                            ToggleButton(
                                colors = ToggleButtonDefaults.toggleButtonColors(
                                    containerColor = MaterialTheme.colorScheme.background
                                ),
                                checked = index == 1,
                                onCheckedChange = {},
                                shapes = when (index) {
                                    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                                    2 -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                                    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                                }
                            ) {
                                Text(label)
                            }
                        }
                    }
                    Text(
                        "Berikut ringkasan pemasukan, pengeluaran, dan transaksi terbaru Anda pada Senin, 12 Juni 2024",
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

            itemsIndexed(uiState.data?.latestTransactions ?: emptyList()) { index, transaction ->
                TransactionListItem(
                    modifier = Modifier
                        .padding(
                            top = when {
                                index == 0 -> 16.dp
                                else -> 2.dp
                            },
                        )
                        .padding(horizontal = 16.dp),
                    transaction = transaction,
                    index = index,
                    size = uiState.data?.latestTransactions?.size ?: 0,
                    onClick = {},
                    onLongClick = onLongClickTransaction
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
        Content(
            onClickRetry = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadingPreview() {
    ArtaTheme {
        Content(
            uiState = CashDashboardUiState(
                isLoading = true
            ),
            onClickRetry = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorPreview() {
    ArtaTheme {
        Content(
            uiState = CashDashboardUiState(
                errorMessage = stringResource(R.string.client_error)
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyPreview() {
    ArtaTheme {
        Content(
            onClickRetry = {},
        )
    }
}

