package id.my.rizalanggoro.arta.feature.gold.presentation.pricehistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.rizalanggoro.arta.core.application.route.GoldRoute
import id.my.rizalanggoro.arta.core.data.AuthPrefs
import id.my.rizalanggoro.arta.core.extension.authorization
import id.my.rizalanggoro.arta.core.extension.errorMessage
import id.my.rizalanggoro.arta.openapi.apis.DashboardApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = PriceHistoryVM.Factory::class)
class PriceHistoryVM @AssistedInject constructor(
    @Assisted private val navKey: GoldRoute.PriceHistory,
    private val dashboardApi: DashboardApi,
    private val authPrefs: AuthPrefs,
) : ViewModel() {
    @AssistedFactory
    interface Factory {
        fun create(navKey: GoldRoute.PriceHistory): PriceHistoryVM
    }

    private val _uiState = MutableStateFlow(PriceHistoryUiState(type = navKey.type))
    val uiState = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    fun loadHistory() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                )
            }

            runCatching {
                val response = dashboardApi.getPriceHistory(
                    authorization = authPrefs.authorization(),
                    type = navKey.type.queryValue,
                    days = 7,
                )
                if (!response.isSuccessful) throw IllegalStateException(response.errorMessage())
                response.body() ?: throw IllegalStateException("Response body is null")
            }.onSuccess { body ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        points = body.data,
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Gagal memuat riwayat harga",
                    )
                }
            }
        }
    }
}