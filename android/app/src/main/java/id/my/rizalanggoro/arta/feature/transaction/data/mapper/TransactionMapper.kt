package id.my.rizalanggoro.arta.feature.transaction.data.mapper

import id.my.rizalanggoro.arta.domain.Transaction
import id.my.rizalanggoro.arta.feature.transaction.data.dto.TransactionDto

fun TransactionDto.toDomain(): Transaction {
    return Transaction(
        id = id,
        walletId = walletId,
        amount = amount,
        categoryId = categoryId,
        description = description,
        date = date,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}
