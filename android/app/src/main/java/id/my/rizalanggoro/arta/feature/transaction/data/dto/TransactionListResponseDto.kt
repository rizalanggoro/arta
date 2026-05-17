package id.my.rizalanggoro.arta.feature.transaction.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TransactionListResponseDto(
    @SerialName("transactions") val transactions: List<TransactionResponseDto> = emptyList(),
)
