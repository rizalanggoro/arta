package id.my.rizalanggoro.arta.feature.home.presentation.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import id.my.rizalanggoro.arta.core.application.MyApplication
import id.my.rizalanggoro.arta.core.data.AuthPrefs
import id.my.rizalanggoro.arta.core.data.SelectedWalletPrefs
import id.my.rizalanggoro.arta.core.data.ThemePrefs
import id.my.rizalanggoro.arta.feature.auth.data.AuthRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeSettingVM(
    private val authPrefs: AuthPrefs,
    private val themePrefs: ThemePrefs,
    private val selectedWalletPrefs: SelectedWalletPrefs,
    private val authRepository: AuthRepository
) : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as MyApplication
                HomeSettingVM(
                    authPrefs = app.authPrefs,
                    themePrefs = app.themePrefs,
                    selectedWalletPrefs = app.selectedWalletPrefs,
                    authRepository = app.authRepository
                )
            }
        }
    }

    private val _uiState = MutableStateFlow(HomeSettingUiState())
    val uiState: StateFlow<HomeSettingUiState> = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<HomeSettingEvent>()
    val event = _event.asSharedFlow()

    fun onToggleTheme(isDarkTheme: Boolean) = themePrefs.saveDarkTheme(
        isDarkTheme = isDarkTheme
    )

    fun onChangeLogoutDialog(isOpen: Boolean) = _uiState.update {
        it.copy(isLogoutOpen = isOpen)
    }

    fun logout() {
        viewModelScope.launch {
            runCatching {
                authRepository.logout()
            }.onSuccess {
                selectedWalletPrefs.clear()
                authPrefs.clear()
                _event.emit(HomeSettingEvent.LoggedOut)
            }
        }
    }

    init {
        viewModelScope.launch {
            combine(
                authPrefs.currentSession,
                themePrefs.isDarkTheme,
            ) { session, isDarkTheme ->
                HomeSettingUiState(
                    session = session,
                    isDarkTheme = isDarkTheme,
                )
            }.collect { state ->
                _uiState.update {
                    it.copy(
                        session = state.session,
                        isDarkTheme = state.isDarkTheme,
                    )
                }
            }
        }
    }
}

sealed class HomeSettingEvent {
    data object LoggedOut : HomeSettingEvent()
}