package id.my.rizalanggoro.arta.feature.home.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CashDashboardResponseDto(
    @SerialName("active_wallet_name") val activeWalletName: String,
    @SerialName("financial_summary") val financialSummary: CashDashboardFinancialSummaryDto,
    @SerialName("recent_transactions") val recentTransactions: List<CashDashboardTransactionDto> = emptyList(),
)

@Serializable
data class CashDashboardFinancialSummaryDto(
    @SerialName("current_balance") val currentBalance: Double,
    @SerialName("today_income") val todayIncome: Double,
    @SerialName("today_expense") val todayExpense: Double,
)

@Serializable
data class CashDashboardTransactionDto(
    @SerialName("data") val data: CashDashboardTransactionDataDto,
    @SerialName("category") val category: CashDashboardCategoryDto,
)

@Serializable
data class CashDashboardTransactionDataDto(
    @SerialName("id") val id: Int,
    @SerialName("wallet_id") val walletId: Int,
    @SerialName("amount") val amount: Double,
    @SerialName("category_id") val categoryId: Int,
    @SerialName("description") val description: String = "",
    @SerialName("date") val date: String,
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("updated_at") val updatedAt: String = "",
)

@Serializable
data class CashDashboardCategoryDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String = "",
    @SerialName("type") val type: String = "",
)