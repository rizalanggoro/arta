package id.my.rizalanggoro.arta.feature.gold.presentation.detail

import id.my.rizalanggoro.arta.feature.gold.presentation.pricehistory.PriceRange
import id.my.rizalanggoro.arta.openapi.models.DomainGold
import id.my.rizalanggoro.arta.openapi.models.DtoPricePoint

data class GoldDetailUiState(
    val gold: DomainGold? = null,
    val sellPrice: Double = 0.0,
    val profit: Double = 0.0,
    val isLoading: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val walletId: Int? = null,
    val chartPoints: List<DtoPricePoint> = emptyList(),
    val chartRange: PriceRange = PriceRange.ONE_MONTH,
    val isLoadingChart: Boolean = false,
)
