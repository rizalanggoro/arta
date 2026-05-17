package id.my.rizalanggoro.arta.feature.transaction.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateTransactionRequestDto(
    @SerialName("wallet_id") val walletId: Int? = null,
    @SerialName("amount") val amount: Double? = null,
    @SerialName("category_id") val categoryId: Int? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("date") val date: String? = null,
)
