package id.my.rizalanggoro.arta.domain

import kotlinx.serialization.Serializable

@Serializable
data class AuthSession(
    val userId: String,
    val email: String,
    val name: String,
    val currency: String,
    val token: String,
    val tokenType: String,
    val expiresAt: String,
)