package id.my.rizalanggoro.arta.feature.home.presentation.transaction

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import id.my.rizalanggoro.arta.R
import id.my.rizalanggoro.arta.core.constant.TransactionTimeRangeType
import id.my.rizalanggoro.arta.core.data.AuthPrefs
import id.my.rizalanggoro.arta.core.data.SelectedWalletPrefs
import id.my.rizalanggoro.arta.core.data.TransactionFilterPrefs
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.core.extension.authorization
import id.my.rizalanggoro.arta.core.extension.errorMessage
import id.my.rizalanggoro.arta.core.extension.toApiFormat
import id.my.rizalanggoro.arta.openapi.apis.TransactionApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

@HiltViewModel
class HomeTransactionVM @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val transactionApi: TransactionApi,
    private val authPrefs: AuthPrefs,
    private val selectedWalletPrefs: SelectedWalletPrefs,
    private val transactionFilterPrefs: TransactionFilterPrefs,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeTransactionUiState())
    val uiState = _uiState.asStateFlow()

    fun loadTransactions() = viewModelScope.launch {
        val current = _uiState.value
        val walletId = current.selectedWallet?.id ?: return@launch

        runCatching {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            val response = transactionApi.listTransactions(
                authorization = authPrefs.authorization(),
                walletId = walletId,
                includeCategory = true,
                startDate = current.startDateMillis.toApiFormat(),
                endDate = current.endDateMillis.toApiFormat(),
            )
            if (!response.isSuccessful) {
                throw IllegalStateException(response.errorMessage())
            }

            response.body() ?: throw IllegalStateException(
                context.getString(
                    R.string.server_empty_error
                )
            )
        }.onSuccess { body ->
            _uiState.update {
                it.copy(transactions = body)
            }
        }.onFailure { throwable ->
            _uiState.update {
                it.copy(
                    errorMessage = throwable.message ?: context.getString(
                        R.string.client_error
                    )
                )
            }
        }.also {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun parseTimeRange() = _uiState.update {
        val now = LocalDate.now()
        val offset = it.timeRangeOffset.toLong()

        val start = when (it.timeRange) {
            TransactionTimeRangeType.DAILY -> now
                .plusDays(offset)
                .atStartOfDay(ZoneId.systemDefault())

            TransactionTimeRangeType.WEEKLY -> now
                .plusWeeks(offset)
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .atStartOfDay(ZoneId.systemDefault())

            TransactionTimeRangeType.MONTHLY -> now
                .plusMonths(offset)
                .withDayOfMonth(1)
                .atStartOfDay(ZoneId.systemDefault())
        }
        val end = when (it.timeRange) {
            TransactionTimeRangeType.DAILY -> start.plusDays(1)
            TransactionTimeRangeType.WEEKLY -> start.plusWeeks(1)
            TransactionTimeRangeType.MONTHLY -> start.plusMonths(1)
        }

        it.copy(
            startDateMillis = start.toInstant().toEpochMilli(),
            endDateMillis = end.toInstant().toEpochMilli()
        )
    }

    fun onNextTimeRangeClicked() {
        _uiState.update {
            it.copy(timeRangeOffset = it.timeRangeOffset + 1)
        }

        parseTimeRange()
        loadTransactions()
    }

    fun onPrevTimeRangeClicked() {
        _uiState.update {
            it.copy(timeRangeOffset = it.timeRangeOffset - 1)
        }

        parseTimeRange()
        loadTransactions()
    }

    init {
        viewModelScope.launch {
            combine(
                transactionFilterPrefs.groupBy,
                transactionFilterPrefs.timeRange
            ) { groupBy, timeRange ->
                Pair(groupBy, timeRange)
            }.collect { state ->
                _uiState.update {
                    val timeRangeOffset = when {
                        it.timeRange == state.second -> it.timeRangeOffset
                        else -> 0
                    }

                    it.copy(
                        groupBy = state.first,
                        timeRange = state.second,
                        timeRangeOffset = timeRangeOffset
                    )
                }

                parseTimeRange()
                loadTransactions()
            }
        }

        viewModelScope.launch {
            selectedWalletPrefs.selectedWallet.collect { wallet ->
                _uiState.update {
                    it.copy(selectedWallet = wallet)
                }

                loadTransactions()
            }
        }

        viewModelScope.launch {
            AppEventBus.event
                .filter { it is AppEvent.TransactionChanged || it is AppEvent.CategoryChanged }
                .collect { loadTransactions() }
        }
    }
}
