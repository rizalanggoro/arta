package id.my.rizalanggoro.arta.domain

import kotlinx.serialization.Serializable

@Serializable
data class GoldPrice(
    val id: Int,
    val date: String,
    val price: Double,
    val currency: String,
    val createdAt: String = "",
)
