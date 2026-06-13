package id.my.rizalanggoro.arta.core.utils

import id.my.rizalanggoro.arta.openapi.models.DomainCategory
import id.my.rizalanggoro.arta.openapi.models.DomainTransaction
import id.my.rizalanggoro.arta.openapi.models.DtoCategory
import id.my.rizalanggoro.arta.openapi.models.DtoTransaction

object Samples {
    val domainCategories = List(5) {
        DomainCategory(
            createdAt = "2024-06-01T12:00:00Z",
            id = it,
            name = "Makanan dan minuman",
            type = "expense",
            updatedAt = "2024-06-01T12:00:00Z",
            userId = 1
        )
    }

    val domainTransactions = List(5) {
        DomainTransaction(
            amount = (it + 1) * 10000.0,
            categoryId = domainCategories[it].id,
            createdAt = "2024-06-01T12:00:00Z",
            date = "2024-06-01T12:00:00Z",
            description = "Transaksi ${it + 1}",
            id = it,
            updatedAt = "2024-06-01T12:00:00Z",
            walletId = 1
        )
    }

    val dtoCategories = List(5) {
        DtoCategory(
            data = domainCategories[it],
            totalAmount = (it + 1) * 10000.0,
            transactionCount = it + 1,
            transactions = domainTransactions
        )
    }

    val dtoTransactions = List(5) {
        DtoTransaction(
            category = domainCategories[it],
            data = domainTransactions[it]
        )
    }
}