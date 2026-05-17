package id.my.rizalanggoro.arta.feature.home.data.mapper

import id.my.rizalanggoro.arta.domain.CashDashboardOverview
import id.my.rizalanggoro.arta.domain.CashDashboardTransaction
import id.my.rizalanggoro.arta.feature.home.data.dto.CashDashboardCategoryDto
import id.my.rizalanggoro.arta.feature.home.data.dto.CashDashboardResponseDto
import id.my.rizalanggoro.arta.feature.home.data.dto.CashDashboardTransactionDataDto
import id.my.rizalanggoro.arta.feature.home.data.dto.CashDashboardTransactionDto

fun CashDashboardResponseDto.toDomain(): CashDashboardOverview {
    return CashDashboardOverview(
        activeWalletName = activeWalletName,
        currentBalance = financialSummary.currentBalance,
        todayIncome = financialSummary.todayIncome,
        todayExpense = financialSummary.todayExpense,
        recentTransactions = recentTransactions.map { it.toDomain() },
    )
}

fun CashDashboardTransactionDto.toDomain(): CashDashboardTransaction {
    return CashDashboardTransaction(
        id = data.id,
        walletId = data.walletId,
        amount = data.amount,
        categoryId = data.categoryId,
        categoryName = category.name,
        categoryType = category.type,
        description = data.description,
        date = data.date,
        createdAt = data.createdAt,
        updatedAt = data.updatedAt,
    )
}

fun CashDashboardTransactionDataDto.toDomain(category: CashDashboardCategoryDto): CashDashboardTransaction {
    return CashDashboardTransaction(
        id = id,
        walletId = walletId,
        amount = amount,
        categoryId = categoryId,
        categoryName = category.name,
        categoryType = category.type,
        description = description,
        date = date,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}