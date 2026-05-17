package id.my.rizalanggoro.arta.feature.auth.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponseDto(
    @SerialName("user_id") val userId: String,
    @SerialName("email") val email: String,
    @SerialName("name") val name: String,
    @SerialName("token") val token: String,
)