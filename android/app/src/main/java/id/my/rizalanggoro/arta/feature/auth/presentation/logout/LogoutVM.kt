package id.my.rizalanggoro.arta.feature.auth.presentation.logout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.rizalanggoro.arta.core.data.SelectedWalletPrefs
import id.my.rizalanggoro.arta.feature.auth.domain.usecase.LogoutUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogoutVM @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val selectedWalletPrefs: SelectedWalletPrefs,
) : ViewModel() {
    private var _uiState = MutableStateFlow(LogoutUiState())
    val uiState = _uiState.asStateFlow()

    private var _event = MutableSharedFlow<LogoutUiState.Event>()
    val event = _event.asSharedFlow()

    fun logout() = viewModelScope.launch {
        _uiState.update {
            it.copy(isLoading = true)
        }

        runCatching {
            logoutUseCase()
            selectedWalletPrefs.clear()
        }.onSuccess {
            _event.emit(LogoutUiState.Event.LogoutSucceeded)
        }.onFailure {
        }.also {
            _uiState.update {
                it.copy(isLoading = false)
            }
        }
    }
}