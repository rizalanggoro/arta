package id.my.rizalanggoro.arta.feature.home.presentation.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.rizalanggoro.arta.core.data.AuthPrefs
import id.my.rizalanggoro.arta.core.data.SelectedWalletPrefs
import id.my.rizalanggoro.arta.core.data.ThemePrefs
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.openapi.apis.AuthApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeSettingVM @Inject constructor(
    private val authPrefs: AuthPrefs,
    private val themePrefs: ThemePrefs,
    private val selectedWalletPrefs: SelectedWalletPrefs,
    private val authApi: AuthApi,
) : ViewModel() {
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
                val session = authPrefs.currentSession.value
                    ?: throw IllegalStateException("Sesi login tidak ditemukan")

                val response = authApi.apiAuthLogoutPost("Bearer ${session.token}")
                if (!response.isSuccessful) {
                    throw IllegalStateException("Logout gagal")
                }
            }.onSuccess {
                selectedWalletPrefs.clear()
                authPrefs.clear()
                _event.emit(HomeSettingEvent.LoggedOut)
            }.onFailure { throwable ->
                _event.emit(HomeSettingEvent.LogoutFailed(throwable.message ?: "Logout gagal"))
            }
        }
    }

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

sealed class HomeSettingEvent {
    data object LoggedOut : HomeSettingEvent()
    data class LogoutFailed(val message: String) : HomeSettingEvent()
}