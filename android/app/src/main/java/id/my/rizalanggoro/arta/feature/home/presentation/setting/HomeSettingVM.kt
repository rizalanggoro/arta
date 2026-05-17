package id.my.rizalanggoro.arta.feature.home.presentation.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import id.my.rizalanggoro.arta.core.application.MyApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeSettingVM(
    private val app: MyApplication,
) : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as MyApplication
                HomeSettingVM(app = app)
            }
        }
    }

    private val _uiState = MutableStateFlow(HomeSettingUiState())
    val uiState: StateFlow<HomeSettingUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                app.authPrefs.currentSession,
                app.themePrefs.isDarkTheme,
            ) { session, isDarkTheme ->
                HomeSettingUiState(
                    sessionName = session?.name ?: "-",
                    sessionEmail = session?.email ?: "-",
                    isDarkTheme = isDarkTheme,
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun onToggleTheme(isDarkTheme: Boolean) {
        app.themePrefs.saveDarkTheme(isDarkTheme)
    }

    fun onLogout() {
        app.authPrefs.clear()
        _uiState.update {
            it.copy(
                sessionName = "-",
                sessionEmail = "-",
            )
        }
    }
}