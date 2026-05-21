package id.my.rizalanggoro.arta.feature.gold.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GoldTaxPreferenceDto(
	@SerialName("id") val id: Int,
	@SerialName("user_id") val userId: Int,
	@SerialName("carat") val carat: Double,
	@SerialName("tax_rate") val taxRate: Double,
	@SerialName("created_at") val createdAt: String = "",
	@SerialName("updated_at") val updatedAt: String = "",
)