package id.my.rizalanggoro.arta.domain

import kotlinx.serialization.Serializable

@Serializable
data class CashDashboardOverview(
    val activeWalletName: String,
    val currentBalance: Double,
    val todayIncome: Double,
    val todayExpense: Double,
    val recentTransactions: List<CashDashboardTransaction> = emptyList(),
)

@Serializable
data class CashDashboardTransaction(
    val id: Int,
    val walletId: Int,
    val amount: Double,
    val categoryId: Int,
    val categoryName: String,
    val categoryType: String,
    val description: String = "",
    val date: String,
    val createdAt: String = "",
    val updatedAt: String = "",
)