package id.my.rizalanggoro.arta.feature.wallet.presentation.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import id.my.rizalanggoro.arta.core.application.MyApplication
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.feature.wallet.data.WalletRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateWalletVM(
    private val walletRepository: WalletRepository,
) : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as MyApplication)
                CreateWalletVM(
                    walletRepository = app.walletRepository,
                )
            }
        }
    }

    private val _uiState = MutableStateFlow(CreateWalletUiState())
    val uiState: StateFlow<CreateWalletUiState> = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<CreateWalletEvent>()
    val event: SharedFlow<CreateWalletEvent> = _event.asSharedFlow()

    fun onChangeName(value: String) {
        _uiState.update { it.copy(name = value, nameError = null) }
    }

    fun onChangeType(value: String) {
        _uiState.update { it.copy(type = value) }
    }

    fun create() {
        val current = _uiState.value

        if (current.name.isBlank()) {
            _uiState.update { it.copy(nameError = "Nama dompet tidak boleh kosong!") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            walletRepository.createWallet(name = current.name, type = current.type)
                .onSuccess {
                    _event.emit(CreateWalletEvent.Succeeded)
                    AppEventBus.emit(AppEvent.WalletChanged)
                }
            _uiState.update { it.copy(isLoading = false) }
        }
    }
}

sealed class CreateWalletEvent {
    object Succeeded : CreateWalletEvent()
}