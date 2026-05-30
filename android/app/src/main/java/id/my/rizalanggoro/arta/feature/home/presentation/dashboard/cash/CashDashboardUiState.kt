package id.my.rizalanggoro.arta.feature.home.presentation.dashboard.cash

import id.my.rizalanggoro.arta.openapi.models.DomainWallet
import id.my.rizalanggoro.arta.openapi.models.DtoCashDashboard

data class CashDashboardUiState(
    val selectedWallet: DomainWallet? = null,
    val data: DtoCashDashboard? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
