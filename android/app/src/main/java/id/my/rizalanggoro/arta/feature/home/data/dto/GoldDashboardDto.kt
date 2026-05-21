package id.my.rizalanggoro.arta.feature.home.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GoldDashboardResponseDto(
    @SerialName("active_wallet_name") val activeWalletName: String,
    @SerialName("total_asset") val totalAsset: Double,
    @SerialName("buy_price") val buyPrice: Double,
    @SerialName("profit") val profit: Double,
    @SerialName("total_weight") val totalWeight: Double,
    @SerialName("total_gold_items") val totalGoldItems: Int,
    @SerialName("latest_dollar_price") val latestDollarPrice: Double,
    @SerialName("latest_gold_price_per_gram_idr") val latestGoldPricePerGramIdr: Double,
    @SerialName("recent_golds") val recentGolds: List<GoldDashboardGoldDto> = emptyList(),
)

@Serializable
data class GoldDashboardGoldDto(
    @SerialName("data") val data: GoldDashboardGoldDataDto,
)

@Serializable
data class GoldDashboardGoldDataDto(
    @SerialName("id") val id: Int,
    @SerialName("wallet_id") val walletId: Int,
    @SerialName("date") val date: String,
    @SerialName("grams") val grams: Double,
    @SerialName("price") val price: Double,
    @SerialName("type") val type: String,
    @SerialName("carat") val carat: Double = 0.0,
    @SerialName("notes") val notes: String = "",
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("updated_at") val updatedAt: String = "",
)