package id.my.rizalanggoro.arta.feature.auth.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LogoutResponseDto(
    @SerialName("message") val message: String,
)