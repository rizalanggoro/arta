package id.my.rizalanggoro.arta.feature.home.presentation.dashboard.cash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import id.my.rizalanggoro.arta.core.application.MyApplication
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.core.network.RetrofitProvider
import id.my.rizalanggoro.arta.domain.CashDashboard
import id.my.rizalanggoro.arta.feature.home.data.DashboardApiService
import id.my.rizalanggoro.arta.feature.home.data.DashboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToLong

class CashDashboardVM(
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
                CashDashboardVM(
                    dashboardRepository = repository,
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
        viewModelScope.launch {
            dashboardRepository.getCashDashboard()
                .onSuccess { dashboard ->
                    _uiState.value = dashboard.toUiState(sessionName)
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            activeWalletName = "Tabungan Uang",
                            isLoading = false,
                            errorMessage = throwable.message ?: "Gagal memuat dashboard",
                        )
                    }
                }
        }
    }

    private fun CashDashboard.toUiState(sessionName: String?): CashDashboardUiState {
        return CashDashboardUiState(
            activeWalletName = activeWalletName,
            greeting = greetingForName(sessionName),
            balanceDisplay = formatMoney(currentBalance),
            todayIncomeDisplay = formatMoney(todayIncome),
            todayExpenseDisplay = formatMoney(todayExpense),
            recentTransactions = recentTransactions.map { transaction ->
                val category = transaction.category
                CashDashboardTransactionUiState(
                    title = transaction.description.ifBlank {
                        category?.name?.ifBlank { "Transaksi" } ?: "Transaksi"
                    },
                    subtitle = listOfNotNull(
                        category?.name?.takeIf { it.isNotBlank() },
                        formatTransactionDate(transaction.date),
                    ).joinToString(" · "),
                    amountDisplay = buildAmountDisplay(
                        transaction.amount,
                        category?.type.orEmpty()
                    ),
                    isIncome = category?.type == "income",
                )
            },
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

    private fun buildAmountDisplay(amount: Double, categoryType: String): String {
        val prefix = if (categoryType == "income") "+" else "-"
        return "$prefix${formatMoney(amount)}"
    }

    private fun formatTransactionDate(dateValue: String): String {
        val parsed = runCatching { OffsetDateTime.parse(dateValue) }.getOrNull()
            ?: runCatching { java.time.LocalDateTime.parse(dateValue) }.getOrNull()
                ?.atOffset(java.time.ZoneOffset.UTC)
            ?: return dateValue

        val formatter = DateTimeFormatter.ofPattern("dd MMM HH:mm", Locale.forLanguageTag("id-ID"))
        return formatter.format(parsed)
    }

    init {
        loadDashboard()

        viewModelScope.launch {
            AppEventBus.event
                .filter { it is AppEvent.TransactionChanged }
                .collect { loadDashboard() }
        }
    }
}
