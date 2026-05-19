package id.my.rizalanggoro.arta.feature.home.presentation.dashboard.cash

import id.my.rizalanggoro.arta.domain.Wallet
import id.my.rizalanggoro.arta.domain.Transaction

data class CashDashboardUiState(
    val selectedWallet: Wallet? = null,
    val activeWalletName: String = "Tabungan Uang",
    val greeting: String = "Selamat pagi, Pengguna",
    val balanceDisplay: String = "Rp 0",
    val todayIncomeDisplay: String = "Rp 0",
    val todayExpenseDisplay: String = "Rp 0",
    val recentTransactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
