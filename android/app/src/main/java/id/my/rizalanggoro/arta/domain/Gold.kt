package id.my.rizalanggoro.arta.domain

import kotlinx.serialization.Serializable

@Serializable
data class Gold(
    val id: Int,
    val walletId: Int,
    val date: String,
    val grams: Double,
    // `price` is the total purchase price for this gold entry (for the recorded grams)
    val price: Double,
    val type: String,
    val purityPercent: Double = 0.0,
    val notes: String = "",
    val createdAt: String = "",
    val updatedAt: String = "",
)
