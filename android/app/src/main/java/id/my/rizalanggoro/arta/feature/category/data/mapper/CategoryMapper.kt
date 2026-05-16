package id.my.rizalanggoro.arta.feature.category.data.mapper

import id.my.rizalanggoro.arta.domain.Category
import id.my.rizalanggoro.arta.feature.category.data.dto.CategoryDto

fun CategoryDto.toDomain(): Category {
	return Category(
		id = id,
		userId = userId,
		name = name,
		type = type,
		icon = icon,
		color = color,
		isCustom = isCustom,
		isDefault = isDefault,
		createdAt = createdAt,
		updatedAt = updatedAt,
	)
}