package id.my.rizalanggoro.arta.feature.home.presentation.dashboard.cash

data class CashDashboardUiState(
    val activeWalletName: String = "Tabungan Uang",
    val greeting: String = "Selamat pagi, Pengguna",
    val balanceDisplay: String = "Rp 0",
    val todayIncomeDisplay: String = "Rp 0",
    val todayExpenseDisplay: String = "Rp 0",
    val recentTransactions: List<CashDashboardTransactionUiState> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

data class CashDashboardTransactionUiState(
    val title: String,
    val subtitle: String,
    val amountDisplay: String,
    val isIncome: Boolean,
)
