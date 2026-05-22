package id.my.rizalanggoro.arta.feature.home.presentation.dashboard.gold

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import id.my.rizalanggoro.arta.core.application.MyApplication
import id.my.rizalanggoro.arta.domain.GoldDashboard
import id.my.rizalanggoro.arta.feature.home.data.DashboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class GoldDashboardVM(
    private val dashboardRepository: DashboardRepository,
) : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as MyApplication
                GoldDashboardVM(
                    dashboardRepository = app.dashboardRepository,
                )
            }
        }
    }

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
            dashboardRepository.getGoldDashboard()
                .onSuccess { dashboard ->
                    _uiState.value = dashboard.toUiState()
                }
                .onFailure { throwable ->
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

    private fun GoldDashboard.toUiState(): GoldDashboardUiState {
        return GoldDashboardUiState(
            activeWalletName = activeWalletName,
            totalAsset = formatMoney(totalAsset),
            buyPrice = formatMoney(buyPrice),
            profit = formatSignedMoney(profit),
            totalWeight = formatWeight(totalWeight),
            totalGoldItems = "$totalGoldItems item",
            latestDollarPrice = formatMoney(latestDollarPrice),
            latestGoldPricePerGramIdr = formatMoney(latestGoldPricePerGramIdr),
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
