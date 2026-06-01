package id.my.rizalanggoro.arta.feature.home.presentation.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.rizalanggoro.arta.core.data.AuthPrefs
import id.my.rizalanggoro.arta.core.data.ThemePrefs
import id.my.rizalanggoro.arta.core.event.AppEventBus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeSettingVM @Inject constructor(
    private val authPrefs: AuthPrefs,
    private val themePrefs: ThemePrefs,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeSettingUiState())
    val uiState: StateFlow<HomeSettingUiState> = _uiState.asStateFlow()

    fun onToggleTheme(isDarkTheme: Boolean) = themePrefs.saveDarkTheme(
        isDarkTheme = isDarkTheme
    )

    init {
        viewModelScope.launch {
            combine(
                authPrefs.currentSession,
                themePrefs.isDarkTheme,
                AppEventBus.updateEvent,
            ) { session, isDarkTheme, hasUpdate ->
                HomeSettingUiState(
                    session = session,
                    isDarkTheme = isDarkTheme,
                    hasUpdate = hasUpdate
                )
            }.collect { state ->
                _uiState.update {
                    it.copy(
                        session = state.session,
                        isDarkTheme = state.isDarkTheme,
                        hasUpdate = state.hasUpdate
                    )
                }
            }
        }
    }
}