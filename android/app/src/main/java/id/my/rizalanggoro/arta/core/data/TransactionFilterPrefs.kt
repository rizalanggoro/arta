package id.my.rizalanggoro.arta.core.data

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import id.my.rizalanggoro.arta.core.constant.TransactionGroupType
import id.my.rizalanggoro.arta.core.constant.TransactionTimeRangeType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionFilterPrefs @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val prefs = context.getSharedPreferences(
        "transaction_filter_prefs",
        Context.MODE_PRIVATE
    )

    private val _groupBy = MutableStateFlow(getGroupBy())
    val groupBy = _groupBy.asStateFlow()

    fun setGroupBy(newValue: TransactionGroupType) {
        prefs.edit { putInt(KEY_GROUP_BY, newValue.ordinal) }
        _groupBy.update { newValue }
    }

    private fun getGroupBy(): TransactionGroupType = TransactionGroupType
        .entries
        .getOrNull(
            prefs.getInt(
                KEY_GROUP_BY,
                0
            )
        ) ?: TransactionGroupType.TRANSACTION

    private val _timeRange = MutableStateFlow(getTimeRange())
    val timeRange = _timeRange.asStateFlow()

    fun setTimeRange(newValue: TransactionTimeRangeType) {
        prefs.edit { putInt(KEY_TIME_RANGE, newValue.ordinal) }
        _timeRange.update { newValue }
    }

    private fun getTimeRange(): TransactionTimeRangeType = TransactionTimeRangeType
        .entries
        .getOrNull(
            prefs.getInt(
                KEY_TIME_RANGE,
                0
            )
        ) ?: TransactionTimeRangeType.DAILY

    companion object {
        private const val KEY_GROUP_BY = "group_by"
        private const val KEY_TIME_RANGE = "time_range"
    }
}