package id.my.rizalanggoro.arta.core.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Deprecated("move using generated dto")
@Serializable
data class ApiErrorDto(
    @SerialName("code") val code: Int,
    @SerialName("message") val message: String,
)