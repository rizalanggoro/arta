package id.my.rizalanggoro.arta.feature.home.presentation.dashboard.cash

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import id.my.rizalanggoro.arta.R
import id.my.rizalanggoro.arta.core.data.AuthPrefs
import id.my.rizalanggoro.arta.core.data.BalanceVisibilityPrefs
import id.my.rizalanggoro.arta.core.data.SelectedWalletPrefs
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.core.extension.authorization
import id.my.rizalanggoro.arta.core.extension.errorMessage
import id.my.rizalanggoro.arta.core.extension.toApiFormat
import id.my.rizalanggoro.arta.core.extension.toIndonesianDate
import id.my.rizalanggoro.arta.feature.home.presentation.dashboard.cash.HomeCashDashboardUiState.TimeFilter
import id.my.rizalanggoro.arta.openapi.apis.DashboardApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

@HiltViewModel
class HomeCashDashboardVM @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val dashboardApi: DashboardApi,
    private val selectedWalletPrefs: SelectedWalletPrefs,
    private val authPrefs: AuthPrefs,
    private val balanceVisibilityPrefs: BalanceVisibilityPrefs,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeCashDashboardUiState())
    val uiState = _uiState.asStateFlow()

    fun timeFilterChanged(timeFilter: TimeFilter) {
        val current = _uiState.value
        if (!current.isRefreshing && current.timeFilter != timeFilter) {
            _uiState.update {
                val now = LocalDate.now()
                val (startDateMillis, endDateMillis, endDateStr) = when (timeFilter) {
                    TimeFilter.Today -> {
                        val start = now.atStartOfDay(ZoneId.systemDefault())
                        val end = start.plusDays(1)

                        Triple(
                            start.toInstant().toEpochMilli(),
                            end.toInstant().toEpochMilli(),
                            ""
                        )
                    }

                    TimeFilter.ThisWeek -> {
                        val start = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                            .atStartOfDay(ZoneId.systemDefault())
                        val end = start.plusWeeks(1)

                        Triple(
                            start.toInstant().toEpochMilli(),
                            end.toInstant().toEpochMilli(),
                            end.minusDays(1).toInstant().toEpochMilli().toIndonesianDate()
                        )
                    }

                    TimeFilter.ThisMonth -> {
                        val start = now.withDayOfMonth(1)
                            .atStartOfDay(ZoneId.systemDefault())
                        val end = start.plusMonths(1)

                        Triple(
                            start.toInstant().toEpochMilli(),
                            end.toInstant().toEpochMilli(),
                            ""
                        )
                    }
                }

                it.copy(
                    timeFilter = timeFilter,
                    startDateMillis = startDateMillis,
                    endDateMillis = endDateMillis,
                    endDateStr = endDateStr
                )
            }

            loadDashboard(isRefresh = true)
        }
    }

    fun onBalanceVisibilityChanged(isVisible: Boolean) = balanceVisibilityPrefs.set(
        isVisible = isVisible
    )

    fun loadDashboard(isRefresh: Boolean = false) =
        viewModelScope.launch {
            val current = _uiState.value
            val walletId = current.selectedWallet?.id ?: return@launch

            _uiState.update {
                it.copy(
                    isLoading = when {
                        isRefresh -> it.isLoading
                        else -> true
                    },
                    isRefreshing = when {
                        isRefresh -> true
                        else -> it.isRefreshing
                    }
                )
            }

            runCatching {
                val response = dashboardApi.getCashDashboard(
                    authPrefs.authorization(),
                    walletId = walletId,
                    startDate = current.startDateMillis.toApiFormat(),
                    endDate = current.endDateMillis.toApiFormat()
                )

                if (!response.isSuccessful) throw IllegalStateException(response.errorMessage())

                response.body() ?: throw IllegalStateException(
                    context.getString(R.string.server_empty_error)
                )
            }.onSuccess { body ->
                _uiState.update {
                    it.copy(
                        data = body.data
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        errorMessage = throwable.message ?: context.getString(
                            R.string.client_error
                        ),
                    )
                }
            }.also {
                _uiState.update {
                    it.copy(
                        isLoading = when {
                            isRefresh -> it.isLoading
                            else -> false
                        },
                        isRefreshing = when {
                            isRefresh -> false
                            else -> it.isRefreshing
                        },
                    )
                }
            }
        }

    init {
        viewModelScope.launch {
            selectedWalletPrefs.selectedWallet.collect { wallet ->
                _uiState.update {
                    it.copy(selectedWallet = wallet)
                }

                loadDashboard()
            }
        }

        viewModelScope.launch {
            balanceVisibilityPrefs.isBalanceVisible.collect { isVisible ->
                _uiState.update {
                    it.copy(isBalanceVisible = isVisible)
                }
            }
        }

        viewModelScope.launch {
            AppEventBus.event.collect {
                if (it in listOf(AppEvent.TransactionChanged, AppEvent.CategoryChanged))
                    loadDashboard(isRefresh = true)
            }
        }
    }
}
