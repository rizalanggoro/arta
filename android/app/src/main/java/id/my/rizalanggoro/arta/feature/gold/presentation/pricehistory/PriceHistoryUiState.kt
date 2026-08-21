package id.my.rizalanggoro.arta.feature.gold.presentation.pricehistory

import id.my.rizalanggoro.arta.core.application.route.GoldRoute
import id.my.rizalanggoro.arta.openapi.models.DtoPricePoint

data class PriceHistoryUiState(
    val type: GoldRoute.PriceHistoryType = GoldRoute.PriceHistoryType.GOLD,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val points: List<DtoPricePoint> = emptyList(),
)