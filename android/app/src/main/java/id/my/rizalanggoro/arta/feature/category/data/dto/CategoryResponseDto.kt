package id.my.rizalanggoro.arta.feature.category.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryResponseDto(
	@SerialName("data") val data: CategoryDto,
)