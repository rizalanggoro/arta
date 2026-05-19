package id.my.rizalanggoro.arta.feature.home.presentation.dashboard.cash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.my.rizalanggoro.arta.domain.Category
import id.my.rizalanggoro.arta.domain.Transaction
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeCashDashboardScreen(vm: HomeCashDashboardVM = viewModel(factory = HomeCashDashboardVM.Factory)) {
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
    recentTransactions: List<Transaction>,
    isLoading: Boolean,
    errorMessage: String?,
    onRetry: () -> Unit = {},
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
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
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
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
                    Text(
                        text = "5 transaksi terbaru",
                        style = MaterialTheme.typography.titleMedium
                    )
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
                                Text(
                                    text = errorMessage,
                                    color = MaterialTheme.colorScheme.error
                                )
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
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(text = value, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun TransactionRow(
    transaction: Transaction,
    modifier: Modifier = Modifier,
) {
    val category = transaction.category
    val title = transaction.description.ifBlank {
        category?.name?.ifBlank { "Transaksi" } ?: "Transaksi"
    }
    val subtitle = listOfNotNull(
        category?.name?.takeIf { it.isNotBlank() },
        formatTransactionDate(transaction.date),
    ).joinToString(" · ")
    val isIncome = category?.type == "income"

    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleSmall)
            Text(text = subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(
            text = buildAmountDisplay(transaction.amount, category?.type.orEmpty()),
            style = MaterialTheme.typography.titleSmall,
            color = if (isIncome) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
        )
    }
}

private fun buildAmountDisplay(amount: Double, categoryType: String): String {
    val prefix = if (categoryType == "income") "+" else "-"
    return "$prefix${formatMoney(amount)}"
}

private fun formatMoney(value: Double): String {
    val rounded = kotlin.math.round(value).toLong()
    val formatter = java.text.NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID")).apply {
        maximumFractionDigits = 0
    }
    return "Rp ${formatter.format(rounded)}"
}

private fun formatTransactionDate(dateValue: String): String {
    val parsed = runCatching { OffsetDateTime.parse(dateValue) }.getOrNull()
        ?: runCatching { java.time.LocalDateTime.parse(dateValue) }.getOrNull()
            ?.atOffset(java.time.ZoneOffset.UTC)
        ?: return dateValue

    val formatter = DateTimeFormatter.ofPattern("dd MMM HH:mm", Locale.forLanguageTag("id-ID"))
    return formatter.format(parsed)
}

private fun sampleTransactions(): List<Transaction> {
    return listOf(
        Transaction(
            id = 1,
            walletId = 1,
            amount = 1_500_000.0,
            categoryId = 1,
            category = Category(id = 1, name = "Transfer masuk", type = "income"),
            description = "Gaji bulanan",
            date = "2026-05-19T09:15:00+07:00",
        ),
        Transaction(
            id = 2,
            walletId = 1,
            amount = 175_000.0,
            categoryId = 2,
            category = Category(id = 2, name = "Supermarket", type = "expense"),
            description = "Belanja kebutuhan pokok",
            date = "2026-05-19T11:20:00+07:00",
        ),
        Transaction(
            id = 3,
            walletId = 1,
            amount = 100_000.0,
            categoryId = 3,
            category = Category(id = 3, name = "Dompet digital", type = "expense"),
            description = "Top up e-wallet",
            date = "2026-05-19T12:10:00+07:00",
        ),
        Transaction(
            id = 4,
            walletId = 1,
            amount = 750_000.0,
            categoryId = 1,
            category = Category(id = 1, name = "Transfer masuk", type = "income"),
            description = "Pemasukan freelance",
            date = "2026-05-19T14:05:00+07:00",
        ),
        Transaction(
            id = 5,
            walletId = 1,
            amount = 55_000.0,
            categoryId = 4,
            category = Category(id = 4, name = "Ojek online", type = "expense"),
            description = "Transport",
            date = "2026-05-19T16:40:00+07:00",
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
            recentTransactions = sampleTransactions(),
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
