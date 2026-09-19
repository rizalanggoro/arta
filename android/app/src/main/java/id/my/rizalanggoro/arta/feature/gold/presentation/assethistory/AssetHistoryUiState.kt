package id.my.rizalanggoro.arta.feature.gold.presentation.assethistory

import id.my.rizalanggoro.arta.feature.gold.presentation.pricehistory.PriceRange

enum class AssetChartMode(val label: String) {
    ASSET("Aset"),
    PROFIT_RP("Untung Rp"),
    PROFIT_PCT("Untung %"),
}

data class AssetPoint(
    val timestamp: String = "",
    val assetValue: Double = 0.0,
    val profitRp: Double = 0.0,
    val profitPct: Double = 0.0,
)

data class AssetHistoryUiState(
    val range: PriceRange = PriceRange.ONE_WEEK,
    val mode: AssetChartMode = AssetChartMode.ASSET,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val points: List<AssetPoint> = emptyList(),
    val totalBuyPrice: Double = 0.0,
)
