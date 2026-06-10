package id.my.rizalanggoro.arta.core.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BalanceVisibilityPrefs @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)

    private val _isDarkTheme = MutableStateFlow(readTheme())
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    fun saveDarkTheme(isDarkTheme: Boolean) {
        prefs.edit().putBoolean(KEY_IS_DARK_THEME, isDarkTheme).apply()
        _isDarkTheme.value = isDarkTheme
    }

    private fun readTheme(): Boolean {
        return prefs.getBoolean(KEY_IS_DARK_THEME, false)
    }

    companion object {
        private const val KEY_IS_DARK_THEME = "is_dark_theme"
    }
}