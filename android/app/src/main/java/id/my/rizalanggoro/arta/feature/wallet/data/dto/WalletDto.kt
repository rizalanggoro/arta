package id.my.rizalanggoro.arta.feature.wallet.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WalletDto(
	@SerialName("ID") val id: Int,
	@SerialName("UserID") val userId: Int,
	@SerialName("Name") val name: String,
	@SerialName("Type") val type: String,
	@SerialName("CreatedAt") val createdAt: String = "",
	@SerialName("UpdatedAt") val updatedAt: String = "",
)
