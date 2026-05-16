package id.my.rizalanggoro.arta.feature.auth.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiErrorDto(
    @SerialName("code") val code: Int,
    @SerialName("message") val message: String,
)