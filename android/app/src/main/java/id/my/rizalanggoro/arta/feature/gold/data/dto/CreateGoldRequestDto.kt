package id.my.rizalanggoro.arta.feature.gold.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateGoldRequestDto(
	@SerialName("wallet_id") val walletId: Int,
	@SerialName("date") val date: String,
	@SerialName("grams") val grams: Double,
	@SerialName("price_per_gram") val pricePerGram: Double,
	@SerialName("type") val type: String,
	@SerialName("purity_percent") val purityPercent: Double = 0.0,
	@SerialName("notes") val notes: String = "",
)
