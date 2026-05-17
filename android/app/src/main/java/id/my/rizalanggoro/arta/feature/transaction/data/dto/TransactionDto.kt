package id.my.rizalanggoro.arta.feature.transaction.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TransactionDto(
    @SerialName("id") val id: Int,
    @SerialName("wallet_id") val walletId: Int,
    @SerialName("amount") val amount: Double,
    @SerialName("category_id") val categoryId: Int = 0,
    @SerialName("description") val description: String = "",
    @SerialName("date") val date: String,
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("updated_at") val updatedAt: String = "",
)
