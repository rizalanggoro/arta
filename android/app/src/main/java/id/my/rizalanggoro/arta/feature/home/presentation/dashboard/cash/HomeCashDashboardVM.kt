package id.my.rizalanggoro.arta.feature.home.presentation.dashboard.cash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import id.my.rizalanggoro.arta.core.application.MyApplication
import id.my.rizalanggoro.arta.core.data.SelectedWalletPrefs
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.domain.AuthSession
import id.my.rizalanggoro.arta.openapi.apis.DashboardApi
import id.my.rizalanggoro.arta.openapi.models.CashDashboardRes
import id.my.rizalanggoro.arta.openapi.models.DomainWallet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.time.LocalTime
import java.util.Locale
import kotlin.math.roundToLong

class HomeCashDashboardVM(
    private val dashboardApi: DashboardApi,
    private val selectedWalletPrefs: SelectedWalletPrefs,
    private val authSessionProvider: () -> AuthSession?,
    private val sessionName: String?,
) : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as MyApplication
                HomeCashDashboardVM(
                    dashboardApi = app.dashboardApi,
                    selectedWalletPrefs = app.selectedWalletPrefs,
                    authSessionProvider = { app.authPrefs.currentSession.value },
                    sessionName = app.authPrefs.currentSession.value?.name,
                )
            }
        }
    }

    private val _uiState = MutableStateFlow(
        CashDashboardUiState(
            activeWalletName = "Memuat...",
            greeting = greetingForName(sessionName),
            isLoading = true,
        ),
    )
    val uiState: StateFlow<CashDashboardUiState> = _uiState.asStateFlow()

    fun retry() {
        loadDashboard()
    }

    private fun loadDashboard() {
        val walletId = _uiState.value.selectedWallet?.id

        viewModelScope.launch {
            runCatching {
                val token = authSessionProvider()?.token ?: throw IllegalStateException("Sesi login tidak ditemukan")
                val response = dashboardApi.getCashDashboard("Bearer $token", walletId)
                if (!response.isSuccessful) throw IllegalStateException(response.errorBody()?.string() ?: "Request failed")
                response.body() ?: throw IllegalStateException("Respons server kosong")
            }.onSuccess { res ->
                _uiState.value = res.toUiState(
                    sessionName = sessionName,
                    selectedWallet = _uiState.value.selectedWallet,
                )
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        activeWalletName = it.selectedWallet?.name ?: "Tabungan Uang",
                        isLoading = false,
                        errorMessage = throwable.message ?: "Gagal memuat dashboard",
                    )
                }
            }
        }
    }

    private fun CashDashboardRes.toUiState(
        sessionName: String?,
        selectedWallet: DomainWallet?,
    ): CashDashboardUiState {
        return CashDashboardUiState(
            selectedWallet = selectedWallet,
            activeWalletName = activeWalletName,
            greeting = greetingForName(sessionName),
            balanceDisplay = formatMoney(financialSummary.currentBalance.toDouble()),
            todayIncomeDisplay = formatMoney(financialSummary.todayIncome.toDouble()),
            todayExpenseDisplay = formatMoney(financialSummary.todayExpense.toDouble()),
            recentTransactions = recentTransactions,
            isLoading = false,
            errorMessage = null,
        )
    }

    private fun greetingForName(name: String?): String {
        val displayName = name?.takeIf { it.isNotBlank() } ?: "Pengguna"
        val greeting = when (LocalTime.now().hour) {
            in 5..10 -> "Selamat pagi"
            in 11..14 -> "Selamat siang"
            else -> "Selamat sore"
        }
        return "$greeting, $displayName"
    }

    private fun formatMoney(value: Double): String {
        val rounded = value.roundToLong()
        val formatter = NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID")).apply {
            maximumFractionDigits = 0
        }
        return "Rp ${formatter.format(rounded)}"
    }

    init {
        viewModelScope.launch {
            selectedWalletPrefs.selectedWallet.collect { wallet ->
                _uiState.update {
                    it.copy(
                        selectedWallet = wallet,
                        activeWalletName = wallet?.name ?: it.activeWalletName,
                        isLoading = true,
                        errorMessage = null,
                    )
                }
                loadDashboard()
            }
        }

        viewModelScope.launch {
            AppEventBus.event
                .filter { it is AppEvent.TransactionChanged }
                .collect { loadDashboard() }
        }
    }
}
