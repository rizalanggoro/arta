package id.my.rizalanggoro.arta.feature.transaction.presentation.action

import id.my.rizalanggoro.arta.core.constant.TransactionGroupType
import id.my.rizalanggoro.arta.core.constant.TransactionTimeRangeType

data class TransactionFilterUiState(
    val groupBy: TransactionGroupType = TransactionGroupType.TRANSACTION,
    val timeRange: TransactionTimeRangeType = TransactionTimeRangeType.DAILY,
)
