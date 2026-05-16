package id.my.rizalanggoro.arta.feature.auth.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponseDto(
    @SerialName("user_id") val userId: String,
    @SerialName("email") val email: String,
    @SerialName("name") val name: String,
    @SerialName("currency") val currency: String,
    @SerialName("token") val token: String,
    @SerialName("token_type") val tokenType: String,
    @SerialName("expires_at") val expiresAt: String,
)