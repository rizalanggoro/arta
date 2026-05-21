package id.my.rizalanggoro.arta.feature.gold.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GoldTaxPreferenceRequestDto(
	@SerialName("carat") val carat: Double,
	@SerialName("tax_rate") val taxRate: Double,
)