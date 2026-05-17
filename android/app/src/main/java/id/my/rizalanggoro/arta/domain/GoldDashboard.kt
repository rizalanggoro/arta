package id.my.rizalanggoro.arta.domain

import kotlinx.serialization.Serializable

@Serializable
data class GoldDashboardOverview(
    val activeWalletName: String,
    val totalAsset: Double,
    val buyPrice: Double,
    val profit: Double,
    val totalWeight: Double,
    val totalGoldItems: Int,
    val latestDollarPrice: Double,
    val latestGoldPricePerGramIdr: Double,
    val recentGolds: List<GoldDashboardGold> = emptyList(),
)

@Serializable
data class GoldDashboardGold(
    val id: Int,
    val walletId: Int,
    val date: String,
    val grams: Double,
    val price: Double,
    val type: String,
    val purityPercent: Double = 0.0,
    val notes: String = "",
    val createdAt: String = "",
    val updatedAt: String = "",
)