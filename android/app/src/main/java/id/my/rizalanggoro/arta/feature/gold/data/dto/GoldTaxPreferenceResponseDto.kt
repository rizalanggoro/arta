package id.my.rizalanggoro.arta.feature.gold.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GoldTaxPreferenceResponseDto(
	@SerialName("preference") val preference: GoldTaxPreferenceDto,
)