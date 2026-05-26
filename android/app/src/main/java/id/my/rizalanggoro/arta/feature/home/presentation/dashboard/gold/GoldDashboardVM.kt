package id.my.rizalanggoro.arta.feature.home.presentation.dashboard.gold

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.rizalanggoro.arta.core.data.AuthPrefs
import id.my.rizalanggoro.arta.core.data.SelectedWalletPrefs
import id.my.rizalanggoro.arta.openapi.apis.DashboardApi
import id.my.rizalanggoro.arta.openapi.models.GoldDashboardRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class GoldDashboardVM @Inject constructor(
    private val dashboardApi: DashboardApi,
    private val authPrefs: AuthPrefs,
    private val selectedWalletPrefs: SelectedWalletPrefs
) : ViewModel() {
    private val _uiState = MutableStateFlow(GoldDashboardUiState())
    val uiState = _uiState.asStateFlow()

    fun retry() {
        loadDashboard()
    }

    private fun loadDashboard() {
        val currentState = _uiState.value
        val selectedWallet = currentState.selectedWallet ?: return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            runCatching {
                val token = authPrefs.currentSession.value?.token
                    ?: throw IllegalStateException("Sesi login tidak ditemukan")
                val response = dashboardApi.getGoldDashboard(
                    authorization = "Bearer $token",
                    walletId = selectedWallet.id
                )
                if (!response.isSuccessful) throw IllegalStateException(
                    response.errorBody()?.string() ?: "Request failed"
                )
                response.body() ?: throw IllegalStateException("Response body is null")
            }.onSuccess { response ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        data = response.data
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Gagal memuat dashboard emas",
                    )
                }
            }
        }
    }

    init {
        viewModelScope.launch {
            selectedWalletPrefs.selectedWallet.collect { wallet ->
                _uiState.update {
                    it.copy(
                        selectedWallet = wallet
                    )
                }

                if (wallet != null) loadDashboard()
            }
        }
    }
}
