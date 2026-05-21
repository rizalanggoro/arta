package id.my.rizalanggoro.arta.feature.gold.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateGoldRequestDto(
    @SerialName("date") val date: String? = null,
    @SerialName("grams") val grams: Double? = null,
    @SerialName("price") val price: Double? = null,
    @SerialName("type") val type: String? = null,
    @SerialName("carat") val carat: Double? = null,
    @SerialName("notes") val notes: String? = null,
)
