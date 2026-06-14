package id.my.rizalanggoro.arta.feature.home.presentation.dashboard.cash

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import id.my.rizalanggoro.arta.R
import id.my.rizalanggoro.arta.core.data.AuthPrefs
import id.my.rizalanggoro.arta.core.data.BalanceVisibilityPrefs
import id.my.rizalanggoro.arta.core.data.CashDashboardTimeFilterPrefs
import id.my.rizalanggoro.arta.core.data.SelectedWalletPrefs
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.core.extension.authorization
import id.my.rizalanggoro.arta.core.extension.errorMessage
import id.my.rizalanggoro.arta.core.extension.toApiFormat
import id.my.rizalanggoro.arta.feature.home.presentation.dashboard.cash.HomeCashDashboardUiState.TimeFilter
import id.my.rizalanggoro.arta.openapi.apis.DashboardApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
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
    private val cashDashboardTimeFilterPrefs: CashDashboardTimeFilterPrefs,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeCashDashboardUiState())
    val uiState = _uiState.asStateFlow()

    private fun getDateRangeByFilter(timeFilter: TimeFilter): Pair<Long, Long> {
        val now = LocalDate.now()
        return when (timeFilter) {
            TimeFilter.Today -> {
                val start = now.atStartOfDay(ZoneId.systemDefault())
                val end = start.plusDays(1)

                Pair(
                    start.toInstant().toEpochMilli(),
                    end.toInstant().toEpochMilli(),
                )
            }

            TimeFilter.ThisWeek -> {
                val start = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                    .atStartOfDay(ZoneId.systemDefault())
                val end = start.plusWeeks(1)

                Pair(
                    start.toInstant().toEpochMilli(),
                    end.toInstant().toEpochMilli(),
                )
            }

            TimeFilter.ThisMonth -> {
                val start = now.withDayOfMonth(1)
                    .atStartOfDay(ZoneId.systemDefault())
                val end = start.plusMonths(1)

                Pair(
                    start.toInstant().toEpochMilli(),
                    end.toInstant().toEpochMilli(),
                )
            }
        }
    }

    fun timeFilterChanged(timeFilter: TimeFilter) {
        val current = _uiState.value
        if (!current.isRefreshing && current.timeFilter != timeFilter) {
            _uiState.update {
                val (startDateMillis, endDateMillis) = getDateRangeByFilter(timeFilter)

                it.copy(
                    timeFilter = timeFilter,
                    startDateMillis = startDateMillis,
                    endDateMillis = endDateMillis,
                )
            }

            cashDashboardTimeFilterPrefs.set(index = timeFilter.ordinal)
            loadDashboard(isRefresh = true)
        }
    }

    fun onBalanceVisibilityChanged(isVisible: Boolean) = balanceVisibilityPrefs.set(
        isVisible = isVisible
    )

    fun loadDashboard(isRefresh: Boolean = false) = viewModelScope.launch {
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
                    data = body
                )
            }
        }.onFailure { throwable ->
            throwable.printStackTrace()
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
            combine(
                selectedWalletPrefs.selectedWallet,
                cashDashboardTimeFilterPrefs.timeFilterIndex,
            ) { selectedWallet, timeFilterIndex ->
                HomeCashDashboardUiState(
                    selectedWallet = selectedWallet,
                    timeFilter = TimeFilter.entries.getOrNull(timeFilterIndex) ?: TimeFilter.Today,
                )
            }.collect { state ->
                _uiState.update {
                    val (startDateMillis, endDateMillis) = getDateRangeByFilter(state.timeFilter)

                    it.copy(
                        selectedWallet = state.selectedWallet,
                        timeFilter = state.timeFilter,
                        startDateMillis = startDateMillis,
                        endDateMillis = endDateMillis,
                    )
                }

                loadDashboard()
            }
        }

        viewModelScope.launch {
            balanceVisibilityPrefs.isBalanceVisible.collect { isBalanceVisible ->
                _uiState.update {
                    it.copy(isBalanceVisible = isBalanceVisible)
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
