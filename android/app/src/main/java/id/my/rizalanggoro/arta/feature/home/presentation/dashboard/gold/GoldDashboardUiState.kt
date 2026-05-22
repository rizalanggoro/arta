package id.my.rizalanggoro.arta.feature.home.presentation.dashboard.gold

import id.my.rizalanggoro.arta.domain.Gold

data class GoldDashboardUiState(
    val activeWalletName: String = "Tabungan Emas",
    val totalAsset: String = "Rp 0",
    val buyPrice: String = "Rp 0",
    val profit: String = "Rp 0",
    val totalWeight: String = "0 g",
    val totalGoldItems: String = "0 item",
    val latestDollarPrice: String = "Rp 0",
    val latestGoldPricePerGramIdr: String = "Rp 0",
    val recentGolds: List<Gold> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
