package id.my.rizalanggoro.arta.feature.home.presentation.dashboard.gold

import id.my.rizalanggoro.arta.openapi.models.DomainWallet
import id.my.rizalanggoro.arta.openapi.models.DtoGoldDashboard

data class GoldDashboardUiState(
    val selectedWallet: DomainWallet? = null,
    val data: DtoGoldDashboard? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
)
