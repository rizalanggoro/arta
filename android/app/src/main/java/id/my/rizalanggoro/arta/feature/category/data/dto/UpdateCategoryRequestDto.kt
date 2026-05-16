package id.my.rizalanggoro.arta.feature.category.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateCategoryRequestDto(
	@SerialName("name") val name: String? = null,
	@SerialName("type") val type: String? = null,
)