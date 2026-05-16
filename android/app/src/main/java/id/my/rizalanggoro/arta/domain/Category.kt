package id.my.rizalanggoro.arta.domain

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: Int,
    val userId: Int? = null,
    val name: String,
    val type: String,
    val icon: String = "",
    val color: String = "",
    val isCustom: Boolean = false,
    val isDefault: Boolean = false,
    val createdAt: String = "",
    val updatedAt: String = "",
)
