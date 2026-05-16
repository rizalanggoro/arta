package id.my.rizalanggoro.arta.feature.wallet.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateWalletRequestDto(
	@SerialName("name") val name: String? = null,
	@SerialName("type") val type: String? = null,
	@SerialName("is_default") val isDefault: Boolean? = null,
)
