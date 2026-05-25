package id.my.rizalanggoro.arta.feature.home.presentation.dashboard.gold

import id.my.rizalanggoro.arta.openapi.models.DomainWallet
import id.my.rizalanggoro.arta.openapi.models.DtoGold

data class GoldDashboardUiState(
    val selectedWallet: DomainWallet? = null,
    val totalAsset: String = "Rp 0",
    val buyPrice: String = "Rp 0",
    val profit: String = "Rp 0",
    val totalWeight: String = "0 g",
    val totalGoldItems: String = "0 item",
    val latestDollarPrice: String = "Rp 0",
    val latestGoldPricePerGramIdr: String = "Rp 0",
    val recentGolds: List<DtoGold> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
