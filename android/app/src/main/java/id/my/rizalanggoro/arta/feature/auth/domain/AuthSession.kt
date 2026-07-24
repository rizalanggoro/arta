package id.my.rizalanggoro.arta.feature.auth.domain

import kotlinx.serialization.Serializable

@Serializable
data class AuthSession(
    val userId: String,
    val email: String,
    val name: String,
    val token: String,
)
