package id.my.rizalanggoro.arta.domain

import kotlinx.serialization.Serializable

@Serializable
data class GoldTaxPreference(
	val id: Int,
	val userId: Int = 0,
	val carat: Double,
	val taxRate: Double,
	val createdAt: String = "",
	val updatedAt: String = "",
)