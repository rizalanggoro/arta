package id.my.rizalanggoro.arta.feature.gold.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.rizalanggoro.arta.core.application.route.GoldRoute
import id.my.rizalanggoro.arta.core.data.AuthPrefs
import id.my.rizalanggoro.arta.core.data.SelectedWalletPrefs
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.feature.gold.presentation.pricehistory.PriceRange
import id.my.rizalanggoro.arta.openapi.apis.DashboardApi
import id.my.rizalanggoro.arta.openapi.apis.GoldApi
import id.my.rizalanggoro.arta.openapi.models.DtoPricePoint
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.min

private const val TROY_OUNCE_IN_GRAMS = 31.1034768

@HiltViewModel
class GoldDetailVM @Inject constructor(
    private val goldApi: GoldApi,
    private val dashboardApi: DashboardApi,
    private val authPrefs: AuthPrefs,
    private val selectedWalletPrefs: SelectedWalletPrefs,
) : ViewModel() {
    private val _uiState = MutableStateFlow(GoldDetailUiState())
    val uiState: StateFlow<GoldDetailUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<GoldDetailEffect>()
    val effect: SharedFlow<GoldDetailEffect> = _effect.asSharedFlow()

    private var lastGoldId: Int? = null

    fun load(goldId: Int) {
        lastGoldId = goldId
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            runCatching {
                val token = authPrefs.currentSession.value?.token
                    ?: throw IllegalStateException("Sesi login tidak ditemukan")
                val response = goldApi.getGold("Bearer $token", goldId)
                if (!response.isSuccessful) throw IllegalStateException(
                    response.errorBody()?.string() ?: "Request failed"
                )
                response.body() ?: throw IllegalStateException("Response body is null")
            }.onSuccess { res ->
                _uiState.update {
                    it.copy(
                        gold = res.data,
                        sellPrice = res.sellPrice,
                        profit = res.profit,
                        isLoading = false,
                    )
                }
                loadItemValueHistory()
            }.onFailure { throwable ->
                _effect.emit(
                    GoldDetailEffect.ShowMessage(throwable.message ?: "Gagal memuat data emas")
                )
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun selectChartRange(range: PriceRange) {
        if (range == _uiState.value.chartRange) return
        _uiState.update { it.copy(chartRange = range) }
        loadItemValueHistory()
    }

    /**
     * Opsi A — anchored: nilai historis item = nilai pasar mentah(t) × k.
     * k menyatukan markup/pajak toko sehingga titik terakhir chart persis
     * sama dengan sellPrice dari server.
     */
    private fun loadItemValueHistory() {
        val state = _uiState.value
        val gold = state.gold ?: return
        val days = state.chartRange.days

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingChart = true, chartPoints = emptyList()) }

            runCatching {
                val token = authPrefs.currentSession.value?.token
                    ?: throw IllegalStateException("Sesi login tidak ditemukan")

                val goldResponse = dashboardApi.getPriceHistory(
                    authorization = "Bearer $token",
                    type = GoldRoute.PriceHistoryType.GOLD.queryValue,
                    days = days,
                )
                if (!goldResponse.isSuccessful) throw IllegalStateException(
                    goldResponse.errorBody()?.string() ?: "Gagal memuat riwayat harga emas"
                )

                val fxResponse = dashboardApi.getPriceHistory(
                    authorization = "Bearer $token",
                    type = GoldRoute.PriceHistoryType.FX.queryValue,
                    days = days,
                )
                if (!fxResponse.isSuccessful) throw IllegalStateException(
                    fxResponse.errorBody()?.string() ?: "Gagal memuat riwayat kurs"
                )

                Pair(
                    goldResponse.body()?.data ?: emptyList(),
                    fxResponse.body()?.data ?: emptyList(),
                )
            }.onSuccess { (goldPoints, fxPoints) ->
                val count = min(goldPoints.size, fxPoints.size)
                val purity = (gold.carat / 24.0).coerceIn(0.0, 1.0)

                val chartPoints = ArrayList<DtoPricePoint>(count)
                var lastMarketItem = 0.0
                for (i in 0 until count) {
                    val marketIdrPerGram =
                        goldPoints[i].value / TROY_OUNCE_IN_GRAMS * fxPoints[i].value
                    val marketItem = marketIdrPerGram * purity * gold.grams
                    if (i == count - 1) lastMarketItem = marketItem
                    chartPoints.add(
                        DtoPricePoint(
                            timestamp = goldPoints[i].timestamp,
                            value = marketItem,
                        )
                    )
                }

                // Replikasi rumus sell price backend (retail multiplier + pajak per karat).
                // Multiplier diturunkan dari snapshot dashboard; pajak dari konfigurasi karat.
                var multiplier = 1.0
                var taxRate = 0.0
                runCatching {
                    val dashToken = authPrefs.currentSession.value?.token
                        ?: throw IllegalStateException("Sesi login tidak ditemukan")
                    val dashResponse = dashboardApi.getGoldDashboard(
                        authorization = "Bearer $dashToken",
                        walletId = state.walletId ?: return@runCatching,
                    )
                    dashResponse.body()?.data?.let { dash ->
                        val marketNowPerGram =
                            dash.goldPrice.pricePerOunceUsd / TROY_OUNCE_IN_GRAMS *
                                dash.fxRate.rate
                        if (marketNowPerGram > 0.0) {
                            multiplier = dash.retailPrice / marketNowPerGram
                        }
                        taxRate = dash.goldTaxes
                            .firstOrNull { it.data.carat == gold.carat }
                            ?.data?.taxRate ?: 0.0
                    }
                }

                // Fallback: beberapa respons server belum mengisi sell_price pada
                // endpoint detail. Pakai estimasi dengan rumus yang sama seperti list.
                val effectiveSellPrice = when {
                    state.sellPrice > 0.0 -> state.sellPrice
                    else -> lastMarketItem * multiplier * (1 - taxRate / 100)
                }

                val k = when {
                    lastMarketItem > 0.0 && effectiveSellPrice > 0.0 ->
                        effectiveSellPrice / lastMarketItem

                    else -> 1.0
                }

                _uiState.update {
                    it.copy(
                        isLoadingChart = false,
                        sellPrice = effectiveSellPrice,
                        profit = when {
                            state.sellPrice > 0.0 -> state.profit
                            else -> effectiveSellPrice - gold.price
                        },
                        chartPoints = chartPoints.map { point ->
                            point.copy(value = point.value * k)
                        },
                    )
                }
            }.onFailure {
                _uiState.update {
                    it.copy(isLoadingChart = false, chartPoints = emptyList())
                }
            }
        }
    }

    fun onDeleteRequested() {
        _uiState.update { it.copy(showDeleteDialog = true) }
    }

    fun dismissDeleteDialog() {
        _uiState.update { it.copy(showDeleteDialog = false) }
    }

    fun confirmDelete() {
        val id = _uiState.value.gold?.id ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            runCatching {
                val token = authPrefs.currentSession.value?.token
                    ?: throw IllegalStateException("Sesi login tidak ditemukan")
                val response = goldApi.deleteGold("Bearer $token", id)
                if (!response.isSuccessful) throw IllegalStateException(
                    response.errorBody()?.string() ?: "Request failed"
                )
            }.onSuccess {
                _effect.emit(GoldDetailEffect.NavigateBack)
            }.onFailure { throwable ->
                _effect.emit(GoldDetailEffect.ShowMessage(throwable.message ?: "Gagal menghapus data emas"))
            }
            _uiState.update { it.copy(isLoading = false, showDeleteDialog = false) }
        }
    }

    init {
        viewModelScope.launch {
            AppEventBus.event.filterIsInstance<AppEvent.GoldChanged>().collect {
                lastGoldId?.let { load(it) }
            }
        }

        viewModelScope.launch {
            selectedWalletPrefs.selectedWallet.collect { wallet ->
                _uiState.update { it.copy(walletId = wallet?.id) }
                if (lastGoldId != null && _uiState.value.gold != null) {
                    loadItemValueHistory()
                }
            }
        }
    }
}

sealed interface GoldDetailEffect {
    data class ShowMessage(val message: String) : GoldDetailEffect
    data object NavigateBack : GoldDetailEffect
}

