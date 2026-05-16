package id.my.rizalanggoro.arta.feature.wallet.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeleteWalletResponseDto(
	@SerialName("message") val message: String,
)
