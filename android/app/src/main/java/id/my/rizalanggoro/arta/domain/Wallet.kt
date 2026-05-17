package id.my.rizalanggoro.arta.domain

import kotlinx.serialization.Serializable

@Serializable
data class Wallet(
    val id: Int,
    val userId: Int,
    val name: String,
    val type: String,
    val createdAt: String = "",
    val updatedAt: String = "",
)
