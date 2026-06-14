package id.my.rizalanggoro.arta.core.data

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CashDashboardTimeFilterPrefs @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val prefs = context.getSharedPreferences(
        "cash_dashboard_time_filter_prefs",
        Context.MODE_PRIVATE
    )

    private val _timeFilterIndex = MutableStateFlow(get())
    val timeFilterIndex = _timeFilterIndex.asStateFlow()

    fun set(index: Int) = prefs.edit {
        putInt(
            KEY_TIME_FILTER_INDEX,
            index
        )
    }.also {
        _timeFilterIndex.update {
            index
        }
    }

    private fun get(): Int = prefs.getInt(
        KEY_TIME_FILTER_INDEX,
        0
    )

    companion object {
        private const val KEY_TIME_FILTER_INDEX = "time_filter_index"
    }
}