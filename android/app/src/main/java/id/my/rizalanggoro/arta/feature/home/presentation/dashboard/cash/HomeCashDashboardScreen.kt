package id.my.rizalanggoro.arta.feature.home.presentation.dashboard.cash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import id.my.rizalanggoro.arta.core.extension.toIndonesianCurrency
import id.my.rizalanggoro.arta.feature.home.presentation.dashboard.cash.component.IncomeExpenseSummary
import id.my.rizalanggoro.arta.openapi.models.CashDashboardResRecentTransactionsInner
import id.my.rizalanggoro.arta.openapi.models.DomainCategory
import id.my.rizalanggoro.arta.openapi.models.DomainTransaction
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme

@Composable
fun HomeCashDashboardScreen(vm: HomeCashDashboardVM = hiltViewModel()) {
    val uiState by vm.uiState.collectAsState()

    Content(
        activeWalletName = uiState.activeWalletName,
        balanceDisplay = uiState.balanceDisplay,
        greeting = uiState.greeting,
        todayIncomeDisplay = uiState.todayIncomeDisplay,
        todayExpenseDisplay = uiState.todayExpenseDisplay,
        recentTransactions = uiState.latestTransactions,
        isLoading = uiState.isLoading,
        errorMessage = uiState.errorMessage,
        onRetry = vm::retry,
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
private fun Content(
    activeWalletName: String,
    balanceDisplay: String,
    greeting: String,
    todayIncomeDisplay: String,
    todayExpenseDisplay: String,
    recentTransactions: List<CashDashboardResRecentTransactionsInner>,
    isLoading: Boolean,
    errorMessage: String?,
    onRetry: () -> Unit = {},
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
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
                    text = 11575000.toIndonesianCurrency(),
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
                    listOf("Hari ini", "Minggu ini", "Bulan ini").forEachIndexed { index, label ->
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
            IncomeExpenseSummary()
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
    }
}

private fun sampleTransactions(): List<CashDashboardResRecentTransactionsInner> {
    return listOf(
        CashDashboardResRecentTransactionsInner(
            category = DomainCategory(
                createdAt = "",
                id = 1,
                name = "Transfer masuk",
                type = "income",
                updatedAt = ""
            ),
            data = DomainTransaction(
                amount = 1_500_000.0,
                categoryId = 1,
                createdAt = "2026-05-19T09:15:00+07:00",
                date = "2026-05-19T09:15:00+07:00",
                description = "Gaji bulanan",
                id = 1,
                updatedAt = "2026-05-19T09:15:00+07:00",
                walletId = 1,
            ),
        ),
        CashDashboardResRecentTransactionsInner(
            category = DomainCategory(
                createdAt = "",
                id = 2,
                name = "Supermarket",
                type = "expense",
                updatedAt = ""
            ),
            data = DomainTransaction(
                amount = 175_000.0,
                categoryId = 2,
                createdAt = "2026-05-19T11:20:00+07:00",
                date = "2026-05-19T11:20:00+07:00",
                description = "Belanja kebutuhan pokok",
                id = 2,
                updatedAt = "2026-05-19T11:20:00+07:00",
                walletId = 1,
            ),
        ),
    )
}

@Preview(showBackground = true, name = "Dashboard Uang - Pagi")
@Composable
private fun HomeCashDashboardPreviewMorning() {
    ArtaTheme {
        Content(
            activeWalletName = "Tabungan Uang",
            greeting = "Selamat pagi, Rizal",
            balanceDisplay = "Rp 12.450.000",
            todayIncomeDisplay = "Rp 1.250.000",
            todayExpenseDisplay = "Rp 430.000",
            recentTransactions = sampleTransactions(),
            isLoading = false,
            errorMessage = null,
            onRetry = {},
        )
    }
}

@Preview(showBackground = true, name = "Dashboard Uang - Empty")
@Composable
private fun HomeCashDashboardPreviewEmpty() {
    ArtaTheme {
        Content(
            activeWalletName = "Tabungan Uang",
            greeting = "Selamat pagi, Rizal",
            balanceDisplay = "Rp 12.450.000",
            todayIncomeDisplay = "Rp 0",
            todayExpenseDisplay = "Rp 0",
            recentTransactions = emptyList(),
            isLoading = false,
            errorMessage = null,
            onRetry = {},
        )
    }
}

@Preview(showBackground = true, name = "Dashboard Uang - Loading")
@Composable
private fun HomeCashDashboardPreviewLoading() {
    ArtaTheme {
        Content(
            activeWalletName = "Tabungan Uang",
            greeting = "Selamat pagi, Rizal",
            balanceDisplay = "Rp 12.450.000",
            todayIncomeDisplay = "Rp 0",
            todayExpenseDisplay = "Rp 0",
            recentTransactions = emptyList(),
            isLoading = true,
            errorMessage = null,
            onRetry = {},
        )
    }
}

@Preview(showBackground = true, name = "Dashboard Uang - Error")
@Composable
private fun HomeCashDashboardPreviewError() {
    ArtaTheme {
        Content(
            activeWalletName = "Tabungan Uang",
            greeting = "Selamat pagi, Rizal",
            balanceDisplay = "Rp 12.450.000",
            todayIncomeDisplay = "Rp 0",
            todayExpenseDisplay = "Rp 0",
            recentTransactions = emptyList(),
            isLoading = false,
            errorMessage = "Gagal memuat transaksi terbaru.",
            onRetry = {},
        )
    }
}
