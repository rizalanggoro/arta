package id.my.rizalanggoro.arta.feature.home.presentation.dashboard.cash

import id.my.rizalanggoro.arta.openapi.models.CashDashboardResRecentTransactionsInner
import id.my.rizalanggoro.arta.openapi.models.DomainWallet

data class CashDashboardUiState(
    val selectedWallet: DomainWallet? = null,
    val activeWalletName: String = "Tabungan Uang",
    val greeting: String = "Selamat pagi, Pengguna",
    val balanceDisplay: String = "Rp 0",
    val todayIncomeDisplay: String = "Rp 0",
    val todayExpenseDisplay: String = "Rp 0",
    val latestTransactions: List<CashDashboardResRecentTransactionsInner> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
