package id.my.rizalanggoro.arta.feature.wallet.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateWalletRequestDto(
	@SerialName("name") val name: String,
	@SerialName("type") val type: String,
)
