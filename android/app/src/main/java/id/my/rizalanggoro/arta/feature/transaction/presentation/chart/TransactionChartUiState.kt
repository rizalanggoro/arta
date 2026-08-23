package id.my.rizalanggoro.arta.feature.transaction.presentation.chart

import id.my.rizalanggoro.arta.core.constant.TransactionTimeRangeType
import id.my.rizalanggoro.arta.openapi.models.DtoTransaction

data class TransactionChartUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val walletId: Int? = null,
    val transactions: List<DtoTransaction> = emptyList(),
    val selectedCategoryId: Int? = null,
    val timeRange: TransactionTimeRangeType = TransactionTimeRangeType.DAILY,
    val timeRangeOffset: Int = 0,
    val startDateMillis: Long = System.currentTimeMillis(),
    val endDateMillis: Long = System.currentTimeMillis(),
)
