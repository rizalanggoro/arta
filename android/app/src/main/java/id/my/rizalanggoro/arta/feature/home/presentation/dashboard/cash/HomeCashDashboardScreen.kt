package id.my.rizalanggoro.arta.feature.home.presentation.dashboard.cash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme

@Composable
fun HomeCashDashboardScreen(vm: CashDashboardVM = viewModel(factory = CashDashboardVM.Factory)) {
    val uiState by vm.uiState.collectAsState()

    Content(
        activeWalletName = uiState.activeWalletName,
        balanceDisplay = uiState.balanceDisplay,
        greeting = uiState.greeting,
        todayIncomeDisplay = uiState.todayIncomeDisplay,
        todayExpenseDisplay = uiState.todayExpenseDisplay,
        recentTransactions = uiState.recentTransactions,
        isLoading = uiState.isLoading,
        errorMessage = uiState.errorMessage,
        onRetry = vm::retry,
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun Content(
    activeWalletName: String,
    balanceDisplay: String,
    greeting: String,
    todayIncomeDisplay: String,
    todayExpenseDisplay: String,
    recentTransactions: List<CashDashboardTransactionUiState>,
    isLoading: Boolean,
    errorMessage: String?,
    onRetry: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxWidth(),
        topBar = {
            TopAppBar(title = { Text(text = "Tabungan Uang") })
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = activeWalletName,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Text(text = greeting, style = MaterialTheme.typography.headlineSmall)
                        Text(
                            text = "Saldo saat ini",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(text = balanceDisplay, style = MaterialTheme.typography.headlineMedium)
                    }
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    SummaryCard(
                        modifier = Modifier.weight(1f),
                        title = "Pemasukan hari ini",
                        value = todayIncomeDisplay,
                    )
                    SummaryCard(
                        modifier = Modifier.weight(1f),
                        title = "Pengeluaran hari ini",
                        value = todayExpenseDisplay,
                    )
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(text = "5 transaksi terbaru", style = MaterialTheme.typography.titleMedium)
                        val visibleTransactions = recentTransactions.take(5)

                        when {
                            isLoading -> {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    CircularProgressIndicator()
                                    Text(text = "Memuat transaksi terbaru...")
                                }
                            }

                            !errorMessage.isNullOrBlank() -> {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
                                    Button(onClick = onRetry) {
                                        Text("Coba lagi")
                                    }
                                }
                            }

                            recentTransactions.isEmpty() -> {
                                Text(
                                    text = "Belum ada transaksi yang bisa ditampilkan.",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }

                            else -> {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    visibleTransactions.forEachIndexed { index, transaction ->
                                        TransactionRow(transaction = transaction)
                                        if (index < visibleTransactions.lastIndex) {
                                            HorizontalDivider()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(text = title, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = value, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun TransactionRow(
    transaction: CashDashboardTransactionUiState,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = transaction.title, style = MaterialTheme.typography.titleSmall)
            Text(text = transaction.subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(
            text = transaction.amountDisplay,
            style = MaterialTheme.typography.titleSmall,
            color = if (transaction.isIncome) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
        )
    }
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
            recentTransactions = listOf(
                CashDashboardTransactionUiState("Gaji bulanan", "Transfer masuk · 09:15", "+Rp 1.500.000", true),
                CashDashboardTransactionUiState("Belanja kebutuhan pokok", "Supermarket · 11:20", "-Rp 175.000", false),
                CashDashboardTransactionUiState("Top up e-wallet", "Dompet digital · 12:10", "-Rp 100.000", false),
                CashDashboardTransactionUiState("Pemasukan freelance", "Transfer masuk · 14:05", "+Rp 750.000", true),
                CashDashboardTransactionUiState("Transport", "Ojek online · 16:40", "-Rp 55.000", false),
            ),
            isLoading = false,
            errorMessage = null,
            onRetry = {},
        )
    }
}

@Preview(showBackground = true, name = "Dashboard Uang - Siang")
@Composable
private fun HomeCashDashboardPreviewAfternoon() {
    ArtaTheme {
        Content(
            activeWalletName = "Tabungan Uang",
            greeting = "Selamat siang, Rizal",
            balanceDisplay = "Rp 12.450.000",
            todayIncomeDisplay = "Rp 1.250.000",
            todayExpenseDisplay = "Rp 430.000",
            recentTransactions = listOf(
                CashDashboardTransactionUiState("Gaji bulanan", "Transfer masuk · 09:15", "+Rp 1.500.000", true),
                CashDashboardTransactionUiState("Belanja kebutuhan pokok", "Supermarket · 11:20", "-Rp 175.000", false),
                CashDashboardTransactionUiState("Top up e-wallet", "Dompet digital · 12:10", "-Rp 100.000", false),
                CashDashboardTransactionUiState("Pemasukan freelance", "Transfer masuk · 14:05", "+Rp 750.000", true),
                CashDashboardTransactionUiState("Transport", "Ojek online · 16:40", "-Rp 55.000", false),
            ),
            isLoading = false,
            errorMessage = null,
            onRetry = {},
        )
    }
}

@Preview(showBackground = true, name = "Dashboard Uang - Sore")
@Composable
private fun HomeCashDashboardPreviewEvening() {
    ArtaTheme {
        Content(
            activeWalletName = "Tabungan Uang",
            greeting = "Selamat sore, Rizal",
            balanceDisplay = "Rp 12.450.000",
            todayIncomeDisplay = "Rp 1.250.000",
            todayExpenseDisplay = "Rp 430.000",
            recentTransactions = listOf(
                CashDashboardTransactionUiState("Gaji bulanan", "Transfer masuk · 09:15", "+Rp 1.500.000", true),
                CashDashboardTransactionUiState("Belanja kebutuhan pokok", "Supermarket · 11:20", "-Rp 175.000", false),
                CashDashboardTransactionUiState("Top up e-wallet", "Dompet digital · 12:10", "-Rp 100.000", false),
                CashDashboardTransactionUiState("Pemasukan freelance", "Transfer masuk · 14:05", "+Rp 750.000", true),
                CashDashboardTransactionUiState("Transport", "Ojek online · 16:40", "-Rp 55.000", false),
            ),
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
