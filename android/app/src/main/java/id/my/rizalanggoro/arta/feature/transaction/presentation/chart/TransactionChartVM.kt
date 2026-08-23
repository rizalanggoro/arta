package id.my.rizalanggoro.arta.feature.transaction.presentation.chart

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import id.my.rizalanggoro.arta.R
import id.my.rizalanggoro.arta.core.application.route.TransactionRoute
import id.my.rizalanggoro.arta.core.constant.TransactionTimeRangeType
import id.my.rizalanggoro.arta.core.constant.calculateTimeRange
import id.my.rizalanggoro.arta.core.data.AuthPrefs
import id.my.rizalanggoro.arta.core.data.SelectedWalletPrefs
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.core.extension.authorization
import id.my.rizalanggoro.arta.core.extension.errorMessage
import id.my.rizalanggoro.arta.core.extension.toApiFormat
import id.my.rizalanggoro.arta.openapi.apis.TransactionApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = TransactionChartVM.Factory::class)
class TransactionChartVM @AssistedInject constructor(
    @Assisted private val navKey: TransactionRoute.Chart,
    @param:ApplicationContext private val context: Context,
    private val selectedWalletPrefs: SelectedWalletPrefs,
    private val authPrefs: AuthPrefs,
    private val transactionApi: TransactionApi,
) : ViewModel() {
    @AssistedFactory
    interface Factory {
        fun create(navKey: TransactionRoute.Chart): TransactionChartVM
    }

    val chartType: String
        get() = navKey.type

    private var _uiState = MutableStateFlow(
        TransactionChartUiState(
            timeRange = navKey.timeRange,
            timeRangeOffset = navKey.timeRangeOffset,
        )
    )
    val uiState = _uiState.asStateFlow()

    fun onCategorySelected(categoryId: Int?) {
        _uiState.update {
            it.copy(
                selectedCategoryId = when {
                    it.selectedCategoryId == categoryId -> null
                    else -> categoryId
                }
            )
        }
    }

    private fun parseTimeRange() = _uiState.update {
        val (start, end) = calculateTimeRange(it.timeRange, it.timeRangeOffset)

        it.copy(
            startDateMillis = start,
            endDateMillis = end,
        )
    }

    private fun loadTransactions() = viewModelScope.launch {
        val current = _uiState.value
        val walletId = current.walletId ?: return@launch

        runCatching {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val response = transactionApi.listTransactions(
                authorization = authPrefs.authorization(),
                walletId = walletId,
                includeCategory = true,
                startDate = current.startDateMillis.toApiFormat(),
                endDate = current.endDateMillis.toApiFormat(),
            )

            if (!response.isSuccessful) throw IllegalStateException(response.errorMessage())

            val body = response.body() ?: throw IllegalStateException(
                context.getString(R.string.server_empty_error)
            )

            body.filter { (it.category?.type ?: "") == navKey.type }
        }.onSuccess { transactions ->
            _uiState.update {
                it.copy(
                    isLoading = false,
                    transactions = transactions,
                    selectedCategoryId = null,
                )
            }
        }.onFailure { throwable ->
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = throwable.message ?: context.getString(
                        R.string.client_error
                    )
                )
            }
        }
    }

    init {
        parseTimeRange()

        viewModelScope.launch {
            selectedWalletPrefs.selectedWallet.collect { wallet ->
                _uiState.update {
                    it.copy(walletId = wallet?.id, selectedCategoryId = null)
                }
                loadTransactions()
            }
        }

        viewModelScope.launch {
            AppEventBus.event.filterIsInstance<AppEvent.TransactionChanged>().collect {
                loadTransactions()
            }
        }
    }
}
