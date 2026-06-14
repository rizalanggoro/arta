package id.my.rizalanggoro.arta.feature.home.presentation.dashboard.cash

import id.my.rizalanggoro.arta.openapi.models.CashDashboardRes
import id.my.rizalanggoro.arta.openapi.models.DomainWallet
import java.time.LocalDate
import java.time.ZoneId

data class HomeCashDashboardUiState(
    val selectedWallet: DomainWallet? = null,
    val isBalanceVisible: Boolean = true,
    val data: CashDashboardRes? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val timeFilter: TimeFilter = TimeFilter.Today,
    val startDateMillis: Long = LocalDate.now()
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli(),
    val endDateMillis: Long = LocalDate.now()
        .plusDays(1)
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli(),
    val endDateStr: String = "",
) {
    enum class TimeFilter {
        Today, ThisWeek, ThisMonth,
    }
}
