package id.my.rizalanggoro.arta.domain

import kotlinx.serialization.Serializable

@Serializable
data class Session(
    val id: Int,
    val userId: Int,
    val token: String,
    val createdAt: String = "",
)
