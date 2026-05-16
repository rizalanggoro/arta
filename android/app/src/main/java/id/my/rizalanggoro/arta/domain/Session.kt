package id.my.rizalanggoro.arta.domain

import kotlinx.serialization.Serializable

@Serializable
data class Session(
    val id: Int,
    val userId: Int,
    val token: String,
    val tokenType: String,
    val expiresAt: String,
    val revoked: Boolean = false,
    val createdAt: String = "",
)
