package id.my.rizalanggoro.arta.feature.home.presentation.dashboard.gold

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.rizalanggoro.arta.core.data.AuthPrefs
import id.my.rizalanggoro.arta.openapi.apis.DashboardApi
import id.my.rizalanggoro.arta.openapi.models.GoldDashboardRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        GoldDashboardUiState(
            activeWalletName = "Memuat...",
            isLoading = true,
        ),
    )
    val uiState: StateFlow<GoldDashboardUiState> = _uiState.asStateFlow()

    fun retry() {
        loadDashboard()
    }

    private fun loadDashboard() {
        viewModelScope.launch {
            runCatching {
                val token = authPrefs.currentSession.value?.token ?: throw IllegalStateException("Sesi login tidak ditemukan")
                val response = dashboardApi.getGoldDashboard("Bearer $token")
                if (!response.isSuccessful) throw IllegalStateException(response.errorBody()?.string() ?: "Request failed")
                response.body() ?: throw IllegalStateException("Response body is null")
            }.onSuccess { res ->
                _uiState.value = res.toUiState()
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        activeWalletName = "Tabungan Emas",
                        isLoading = false,
                        errorMessage = throwable.message ?: "Gagal memuat dashboard emas",
                    )
                }
            }
        }
    }
    private fun GoldDashboardRes.toUiState(): GoldDashboardUiState {
        return GoldDashboardUiState(
            activeWalletName = activeWalletName,
            totalAsset = formatMoney(totalAsset.toDouble()),
            buyPrice = formatMoney(buyPrice.toDouble()),
            profit = formatSignedMoney(profit.toDouble()),
            totalWeight = formatWeight(totalWeight.toDouble()),
            totalGoldItems = "$totalGoldItems item",
            latestDollarPrice = formatMoney(latestDollarPrice.toDouble()),
            latestGoldPricePerGramIdr = formatMoney(latestGoldPricePerGramIdr.toDouble()),
            recentGolds = recentGolds,
            isLoading = false,
            errorMessage = null,
        )
    }

    private fun formatMoney(value: Double): String {
        val formatter = NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID")).apply {
            maximumFractionDigits = 0
        }
        return "Rp ${formatter.format(value.toLong())}"
    }

    private fun formatSignedMoney(value: Double): String {
        val sign = if (value >= 0) "+" else "-"
        return "$sign${formatMoney(kotlin.math.abs(value))}"
    }

    private fun formatWeight(value: Double): String {
        val formatter = NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID")).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }
        return "${formatter.format(value)} g"
    }

    init {
        loadDashboard()
    }
}
