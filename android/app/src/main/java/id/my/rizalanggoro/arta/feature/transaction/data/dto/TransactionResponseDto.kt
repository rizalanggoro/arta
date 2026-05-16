package id.my.rizalanggoro.arta.feature.transaction.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TransactionResponseDto(
    @SerialName("data") val data: TransactionDto,
)
