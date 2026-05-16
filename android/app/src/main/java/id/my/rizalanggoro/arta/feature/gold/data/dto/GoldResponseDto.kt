package id.my.rizalanggoro.arta.feature.gold.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GoldResponseDto(
	@SerialName("data") val data: GoldDto,
)
