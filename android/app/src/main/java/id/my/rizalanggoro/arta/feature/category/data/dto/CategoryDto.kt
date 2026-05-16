package id.my.rizalanggoro.arta.feature.category.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(
	@SerialName("id") val id: Int,
	@SerialName("user_id") val userId: Int? = null,
	@SerialName("name") val name: String,
	@SerialName("type") val type: String,
	@SerialName("icon") val icon: String = "",
	@SerialName("color") val color: String = "",
	@SerialName("is_custom") val isCustom: Boolean = false,
	@SerialName("is_default") val isDefault: Boolean = false,
	@SerialName("created_at") val createdAt: String = "",
	@SerialName("updated_at") val updatedAt: String = "",
)