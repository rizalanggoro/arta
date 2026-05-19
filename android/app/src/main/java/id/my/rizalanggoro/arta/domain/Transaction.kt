package id.my.rizalanggoro.arta.domain

import kotlinx.serialization.Serializable

@Serializable
data class Transaction(
    val id: Int,
    val walletId: Int,
    val amount: Double,
    val categoryId: Int,
    val category: Category? = null,
    val description: String = "",
    val date: String,
    val createdAt: String = "",
    val updatedAt: String = "",
)
