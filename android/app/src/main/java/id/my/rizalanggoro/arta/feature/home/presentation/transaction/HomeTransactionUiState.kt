package id.my.rizalanggoro.arta.feature.home.presentation.transaction

import id.my.rizalanggoro.arta.core.constant.TransactionGroupType
import id.my.rizalanggoro.arta.core.constant.TransactionTimeRangeType
import id.my.rizalanggoro.arta.openapi.models.DomainWallet
import id.my.rizalanggoro.arta.openapi.models.DtoCategory
import id.my.rizalanggoro.arta.openapi.models.DtoTransaction

data class HomeTransactionUiState(
    val selectedWallet: DomainWallet? = null,
    val categories: List<DtoCategory> = emptyList(),
    val transactions: List<DtoTransaction> = emptyList(),
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val startDateMillis: Long = System.currentTimeMillis(),
    val endDateMillis: Long = System.currentTimeMillis(),
    val groupBy: TransactionGroupType = TransactionGroupType.CATEGORY,
    val timeRange: TransactionTimeRangeType = TransactionTimeRangeType.DAILY,
    val timeRangeOffset: Int = 0,
)
