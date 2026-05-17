package id.my.rizalanggoro.arta.feature.transaction.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateTransactionRequestDto(
    @SerialName("wallet_id") val walletId: Int,
    @SerialName("amount") val amount: Double,
    @SerialName("category_id") val categoryId: Int,
    @SerialName("description") val description: String = "",
    @SerialName("date") val date: String,
)
