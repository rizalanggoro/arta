package id.my.rizalanggoro.arta.feature.category.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryListResponseDto(
	@SerialName("categories") val categories: List<CategoryResponseDto>,
)