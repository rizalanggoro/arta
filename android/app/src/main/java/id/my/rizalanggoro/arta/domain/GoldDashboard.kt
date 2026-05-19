package id.my.rizalanggoro.arta.domain

import kotlinx.serialization.Serializable

@Serializable
data class GoldDashboard(
    val activeWalletName: String,
    val totalAsset: Double,
    val buyPrice: Double,
    val profit: Double,
    val totalWeight: Double,
    val totalGoldItems: Int,
    val latestDollarPrice: Double,
    val latestGoldPricePerGramIdr: Double,
    val recentGolds: List<Gold> = emptyList(),
)
