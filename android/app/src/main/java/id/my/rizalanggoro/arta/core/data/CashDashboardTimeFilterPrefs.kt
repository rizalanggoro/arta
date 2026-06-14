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
        "balance_visibility_prefs",
        Context.MODE_PRIVATE
    )

    private val _isBalanceVisible = MutableStateFlow(get())
    val isBalanceVisible = _isBalanceVisible.asStateFlow()

    fun set(isVisible: Boolean) = prefs.edit {
        putBoolean(
            KEY_IS_BALANCE_VISIBLE,
            isVisible
        )
    }.also {
        _isBalanceVisible.update {
            isVisible
        }
    }

    private fun get(): Boolean = prefs.getBoolean(
        KEY_IS_BALANCE_VISIBLE,
        true
    )

    companion object {
        private const val KEY_IS_BALANCE_VISIBLE = "is_balance_visible"
    }
}