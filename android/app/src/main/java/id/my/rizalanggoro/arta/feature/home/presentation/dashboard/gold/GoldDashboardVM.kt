package id.my.rizalanggoro.arta.feature.home.presentation.dashboard.gold

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import id.my.rizalanggoro.arta.core.application.MyApplication
import id.my.rizalanggoro.arta.core.network.RetrofitProvider
import id.my.rizalanggoro.arta.domain.GoldDashboardOverview
import id.my.rizalanggoro.arta.feature.home.data.DashboardApiService
import id.my.rizalanggoro.arta.feature.home.data.DashboardRepository
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.text.NumberFormat
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GoldDashboardVM(
    private val dashboardRepository: DashboardRepository,
    private val sessionName: String?,
) : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as MyApplication
                val repository = DashboardRepository(
                    apiService = RetrofitProvider.create(DashboardApiService::class.java),
                    authSessionProvider = { app.authPrefs.currentSession.value },
                )
                GoldDashboardVM(
                    dashboardRepository = repository,
                    sessionName = app.authPrefs.currentSession.value?.name,
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

    init {
        loadDashboard()
    }

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

    private fun GoldDashboardOverview.toUiState(): GoldDashboardUiState {
        return GoldDashboardUiState(
            activeWalletName = activeWalletName,
            totalAsset = formatMoney(totalAsset),
            buyPrice = formatMoney(buyPrice),
            profit = formatSignedMoney(profit),
            totalWeight = formatWeight(totalWeight),
            totalGoldItems = "$totalGoldItems item",
            latestDollarPrice = formatMoney(latestDollarPrice),
            latestGoldPricePerGramIdr = formatMoney(latestGoldPricePerGramIdr),
            recentGolds = recentGolds.map { gold ->
                GoldDashboardGoldUiState(
                    title = gold.notes.ifBlank { "Emas #${gold.id}" },
                    subtitle = listOfNotNull(
                        gold.type.replace('_', ' '),
                        formatDate(gold.date),
                    ).joinToString(" · "),
                    amountDisplay = "${formatWeight(gold.grams)} · ${formatMoney(gold.price)}",
                )
            },
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

    private fun formatDate(value: String): String {
        if (value.isBlank()) return "-"

        val formatted = runCatching { OffsetDateTime.parse(value) }.getOrNull()?.toLocalDate()
            ?: runCatching { LocalDateTime.parse(value).toLocalDate() }.getOrNull()
            ?: runCatching { LocalDate.parse(value) }.getOrNull()

        return formatted?.toString() ?: value
    }
}
