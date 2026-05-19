package id.my.rizalanggoro.arta.domain

import kotlinx.serialization.Serializable

@Serializable
data class CashDashboard(
    val activeWalletName: String,
    val currentBalance: Double,
    val todayIncome: Double,
    val todayExpense: Double,
    val recentTransactions: List<Transaction> = emptyList(),
)