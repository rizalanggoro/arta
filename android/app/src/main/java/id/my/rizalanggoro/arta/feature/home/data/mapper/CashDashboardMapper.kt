package id.my.rizalanggoro.arta.feature.home.data.mapper

import id.my.rizalanggoro.arta.domain.CashDashboard
import id.my.rizalanggoro.arta.domain.Category
import id.my.rizalanggoro.arta.domain.Transaction
import id.my.rizalanggoro.arta.feature.home.data.dto.CashDashboardCategoryDto
import id.my.rizalanggoro.arta.feature.home.data.dto.CashDashboardResponseDto
import id.my.rizalanggoro.arta.feature.home.data.dto.CashDashboardTransactionDataDto
import id.my.rizalanggoro.arta.feature.home.data.dto.CashDashboardTransactionDto

fun CashDashboardResponseDto.toDomain(): CashDashboard {
    return CashDashboard(
        activeWalletName = activeWalletName,
        currentBalance = financialSummary.currentBalance,
        todayIncome = financialSummary.todayIncome,
        todayExpense = financialSummary.todayExpense,
        recentTransactions = recentTransactions.map { it.toDomain() },
    )
}

fun CashDashboardTransactionDto.toDomain(): Transaction {
    return Transaction(
        id = data.id,
        walletId = data.walletId,
        amount = data.amount,
        categoryId = data.categoryId,
        category = category.toDomain(),
        description = data.description,
        date = data.date,
        createdAt = data.createdAt,
        updatedAt = data.updatedAt,
    )
}

fun CashDashboardTransactionDataDto.toDomain(category: CashDashboardCategoryDto): Transaction {
    return Transaction(
        id = id,
        walletId = walletId,
        amount = amount,
        categoryId = categoryId,
        category = category.toDomain(),
        description = description,
        date = date,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}

private fun CashDashboardCategoryDto.toDomain(): Category {
    return Category(
        id = id,
        name = name,
        type = type,
    )
}