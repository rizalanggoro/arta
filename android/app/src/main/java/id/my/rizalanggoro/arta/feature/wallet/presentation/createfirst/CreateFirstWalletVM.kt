package id.my.rizalanggoro.arta.feature.wallet.presentation.createfirst

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import id.my.rizalanggoro.arta.core.application.MyApplication
import id.my.rizalanggoro.arta.feature.wallet.walletApiErrorMessage
import id.my.rizalanggoro.arta.openapi.apis.WalletApi
import id.my.rizalanggoro.arta.openapi.models.WalletCreateWalletReq
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateFirstWalletVM(
    private val walletApi: WalletApi,
    private val application: MyApplication,
) : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as MyApplication)
                CreateFirstWalletVM(
                    walletApi = app.walletApi,
                    application = app,
                )
            }
        }
    }

    private val _uiState = MutableStateFlow(CreateFirstWalletUiState())
    val uiState: StateFlow<CreateFirstWalletUiState> = _uiState.asStateFlow()

    private val _messageEvent = MutableSharedFlow<String>()
    val messageEvent: SharedFlow<String> = _messageEvent.asSharedFlow()

    private val _effect = MutableSharedFlow<CreateFirstWalletEffect>()
    val effect: SharedFlow<CreateFirstWalletEffect> = _effect.asSharedFlow()

    fun onChangeName(value: String) {
        _uiState.update { it.copy(name = value, nameError = null) }
    }

    fun onChangeType(value: String) {
        _uiState.update { it.copy(type = value, typeError = null) }
    }

    fun create() {
        val current = _uiState.value
        var hasError = false

        if (current.name.isBlank()) {
            _uiState.update { it.copy(nameError = "Nama wajib diisi") }
            hasError = true
        }

        if (current.type.isBlank()) {
            _uiState.update { it.copy(typeError = "Tipe wajib diisi") }
            hasError = true
        }

        if (hasError) {
            viewModelScope.launch { _messageEvent.emit("Periksa kembali isian wallet pertama") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            runCatching {
                val response = walletApi.createWallet(
                    WalletCreateWalletReq(
                        name = current.name,
                        type = current.type,
                    )
                )

                if (!response.isSuccessful) {
                    throw IllegalStateException(response.walletApiErrorMessage())
                }

                response.body() ?: throw IllegalStateException("Respons server kosong")
            }.onSuccess { response ->
                application.selectedWalletPrefs.saveSelectedWallet(response.data)
                _messageEvent.emit("Wallet pertama dibuat: ${response.data.name.orEmpty()}")
                _effect.emit(CreateFirstWalletEffect.NavigateHome)
            }.onFailure { throwable ->
                _messageEvent.emit(throwable.message ?: "Gagal membuat wallet pertama")
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }
}

sealed class CreateFirstWalletEffect {
    object NavigateHome : CreateFirstWalletEffect()
}