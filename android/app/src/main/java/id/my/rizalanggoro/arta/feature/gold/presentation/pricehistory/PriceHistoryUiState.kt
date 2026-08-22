package id.my.rizalanggoro.arta.feature.gold.presentation.pricehistory

import id.my.rizalanggoro.arta.core.application.route.GoldRoute
import id.my.rizalanggoro.arta.openapi.models.DtoPricePoint

enum class PriceRange(val label: String, val days: Int) {
    ONE_DAY("1 hari", 1),
    ONE_WEEK("1 minggu", 7),
    ONE_MONTH("1 bulan", 30),
    THREE_MONTHS("3 bulan", 90),
    SIX_MONTHS("6 bulan", 180),
    ONE_YEAR("1 tahun", 365),
}

data class PriceHistoryUiState(
    val type: GoldRoute.PriceHistoryType = GoldRoute.PriceHistoryType.GOLD,
    val range: PriceRange = PriceRange.ONE_WEEK,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val points: List<DtoPricePoint> = emptyList(),
)