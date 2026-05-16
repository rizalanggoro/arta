package id.my.rizalanggoro.arta.feature.category.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateCategoryRequestDto(
	@SerialName("name") val name: String,
	@SerialName("type") val type: String,
	@SerialName("icon") val icon: String = "",
	@SerialName("color") val color: String = "",
)