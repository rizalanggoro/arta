package id.my.rizalanggoro.arta.feature.gold.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GoldTaxPreferencesResponseDto(
	@SerialName("preferences") val preferences: List<GoldTaxPreferenceDto> = emptyList(),
)