package id.my.rizalanggoro.arta.feature.gold.presentation.assethistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.rizalanggoro.arta.core.data.AuthPrefs
import id.my.rizalanggoro.arta.core.data.SelectedWalletPrefs
import id.my.rizalanggoro.arta.core.extension.authorization
import id.my.rizalanggoro.arta.core.extension.errorMessage
import id.my.rizalanggoro.arta.feature.gold.presentation.pricehistory.PriceRange
import id.my.rizalanggoro.arta.openapi.apis.DashboardApi
import id.my.rizalanggoro.arta.openapi.models.DtoPricePoint
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class AssetHistoryVM @Inject constructor(
    private val dashboardApi: DashboardApi,
    private val authPrefs: AuthPrefs,
    private val selectedWalletPrefs: SelectedWalletPrefs,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AssetHistoryUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            selectedWalletPrefs.selectedWallet.collect { wallet ->
                if (wallet != null) loadHistory()
            }
        }
    }

    fun selectRange(range: PriceRange) {
        if (range == _uiState.value.range) return
        _uiState.update { it.copy(range = range) }
        loadHistory()
    }

    fun selectMode(mode: AssetChartMode) {
        if (mode == _uiState.value.mode) return
        _uiState.update { it.copy(mode = mode) }
    }

    fun loadHistory() {
        val wallet = selectedWalletPrefs.selectedWallet.value ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching {
                val auth = authPrefs.authorization()
                val dashboardRes = dashboardApi.getGoldDashboard(
                    authorization = auth,
                    walletId = wallet.id,
                )
                if (!dashboardRes.isSuccessful) throw IllegalStateException(dashboardRes.errorMessage())
                val dashboard = dashboardRes.body()?.data
                    ?: throw IllegalStateException("Response body is null")

                val days = _uiState.value.range.days
                val goldDeferred = async {
                    val res = dashboardApi.getPriceHistory(auth, "gold", days)
                    if (!res.isSuccessful) throw IllegalStateException(res.errorMessage())
                    res.body()?.data ?: throw IllegalStateException("Response body is null")
                }
                val fxDeferred = async {
                    val res = dashboardApi.getPriceHistory(auth, "fx", days)
                    if (!res.isSuccessful) throw IllegalStateException(res.errorMessage())
                    res.body()?.data ?: throw IllegalStateException("Response body is null")
                }
                val goldPoints = goldDeferred.await()
                val fxPoints = fxDeferred.await()

                val goldNow = dashboard.goldPrice.pricePerOunceUsd
                val fxNow = dashboard.fxRate.rate.toDouble()
                val scale = if (goldNow > 0 && fxNow > 0) {
                    dashboard.totalAsset / (goldNow * fxNow)
                } else 0.0

                dashboard.totalBuyPrice to joinHistories(
                    goldPoints = goldPoints,
                    fxPoints = fxPoints,
                    scale = scale,
                    totalBuyPrice = dashboard.totalBuyPrice,
                )
            }.onSuccess { (totalBuyPrice, points) ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        points = points,
                        totalBuyPrice = totalBuyPrice,
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Gagal memuat riwayat aset",
                    )
                }
            }
        }
    }

    private fun joinHistories(
        goldPoints: List<DtoPricePoint>,
        fxPoints: List<DtoPricePoint>,
        scale: Double,
        totalBuyPrice: Double,
    ): List<AssetPoint> {
        if (goldPoints.isEmpty() || fxPoints.isEmpty() || scale == 0.0) return emptyList()
        val fxEpochs = fxPoints.map { it.timestamp.toEpochMillis() }
        val fxValues = fxPoints.map { it.value }
        return goldPoints.mapNotNull { gold ->
            val goldEpoch = gold.timestamp.toEpochMillis()
            if (goldEpoch == 0L) return@mapNotNull null
            val fxValue = nearestValue(fxEpochs, fxValues, goldEpoch) ?: return@mapNotNull null
            // ponytail: holdings dianggap konstan, asset(t) = totalAsset_now * gold(t)*fx(t) / (gold_now*fx_now)
            val asset = scale * gold.value * fxValue
            val profitRp = asset - totalBuyPrice
            val profitPct = if (totalBuyPrice != 0.0) profitRp / totalBuyPrice * 100 else 0.0
            AssetPoint(
                timestamp = gold.timestamp,
                assetValue = asset,
                profitRp = profitRp,
                profitPct = profitPct,
            )
        }
    }

    private fun nearestValue(
        epochs: List<Long>,
        values: List<Double>,
        target: Long,
    ): Double? {
        if (epochs.isEmpty()) return null
        var low = 0
        var high = epochs.lastIndex
        while (low <= high) {
            val mid = (low + high) ushr 1
            val midVal = epochs[mid]
            when {
                midVal < target -> low = mid + 1
                midVal > target -> high = mid - 1
                else -> return values[mid]
            }
        }
        val candidates = listOfNotNull(
            high.takeIf { it >= 0 },
            low.takeIf { it <= epochs.lastIndex },
        )
        return candidates.minByOrNull { abs(epochs[it] - target) }?.let(values::get)
    }

    private fun String.toEpochMillis(): Long =
        runCatching { Instant.parse(this).toEpochMilli() }.getOrDefault(0L)
}
