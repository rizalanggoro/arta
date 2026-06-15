package id.my.rizalanggoro.arta.feature.transaction.presentation.action

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.rizalanggoro.arta.core.constant.TransactionGroupType
import id.my.rizalanggoro.arta.core.constant.TransactionTimeRangeType
import id.my.rizalanggoro.arta.core.data.TransactionFilterPrefs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionFilterVM @Inject constructor(
    private val transactionFilterPrefs: TransactionFilterPrefs,
) : ViewModel() {
    private val _uiState = MutableStateFlow(TransactionFilterUiState())
    val uiState = _uiState.asStateFlow()

    fun onGroupByChanged(groupBy: TransactionGroupType) =
        transactionFilterPrefs.setGroupBy(groupBy)

    fun onTimeRangeChanged(timeRange: TransactionTimeRangeType) =
        transactionFilterPrefs.setTimeRange(timeRange)

    init {
        viewModelScope.launch {
            combine(
                transactionFilterPrefs.groupBy,
                transactionFilterPrefs.timeRange
            ) { groupBy, timeRange ->
                TransactionFilterUiState(
                    groupBy = groupBy,
                    timeRange = timeRange,
                )
            }.collect {
                _uiState.value = it
            }
        }
    }
}