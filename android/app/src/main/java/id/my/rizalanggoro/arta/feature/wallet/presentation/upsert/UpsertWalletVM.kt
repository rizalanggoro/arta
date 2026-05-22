package id.my.rizalanggoro.arta.feature.wallet.presentation.upsert

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

class UpsertWalletVM(
    private val walletRepository: WalletRepository,
) : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as MyApplication)
                UpsertWalletVM(walletRepository = app.walletRepository)
            }
        }
    }

    private val _uiState = MutableStateFlow(UpsertWalletUiState())
    val uiState: StateFlow<UpsertWalletUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<UpsertWalletEffect>()
    val effect: SharedFlow<UpsertWalletEffect> = _effect.asSharedFlow()

    fun loadWallet(walletId: Int) {
        if (walletId == 0) {
            _uiState.update {
                it.copy(
                    walletId = 0,
                    name = "",
                    type = "cash_savings",
                    errorMessage = null,
                    isLoading = false,
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    walletId = walletId,
                    isLoading = true,
                    errorMessage = null,
                )
            }

            walletRepository.getWalletById(walletId)
                .onSuccess { wallet ->
                    _uiState.update {
                        it.copy(
                            walletId = wallet.id,
                            name = wallet.name,
                            type = wallet.type,
                            isLoading = false,
                            errorMessage = null,
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.message ?: "Gagal memuat wallet",
                        )
                    }
                }
        }
    }

    fun onChangeName(value: String) {
        _uiState.update { it.copy(name = value, nameError = null) }
    }

    fun onChangeType(value: String) {
        _uiState.update { it.copy(type = value, typeError = null) }
    }

    fun submit() {
        val current = _uiState.value
        val isUpdate = current.walletId > 0

        var hasError = false
        if (current.name.isBlank()) {
            _uiState.update { it.copy(nameError = "Nama wallet wajib diisi") }
            hasError = true
        }
        if (current.type.isBlank()) {
            _uiState.update { it.copy(typeError = "Tipe wallet wajib diisi") }
            hasError = true
        }
        if (hasError) {
            viewModelScope.launch {
                _effect.emit(
                    UpsertWalletEffect.ShowMessage(
                        if (isUpdate) "Periksa kembali wallet yang diubah" else "Periksa kembali wallet yang dibuat"
                    )
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = if (isUpdate) {
                walletRepository.updateWallet(
                    id = current.walletId,
                    name = current.name,
                    type = current.type,
                )
            } else {
                walletRepository.createWallet(
                    name = current.name,
                    type = current.type,
                )
            }

            result
                .onSuccess { wallet ->
                    AppEventBus.emit(AppEvent.WalletChanged)
                    _effect.emit(
                        UpsertWalletEffect.ShowMessage(
                            if (isUpdate) {
                                "Wallet ${wallet.name} berhasil diperbarui"
                            } else {
                                "Wallet ${wallet.name} berhasil dibuat"
                            }
                        )
                    )
                    _effect.emit(UpsertWalletEffect.NavigateBack)
                }
                .onFailure { throwable ->
                    _effect.emit(
                        UpsertWalletEffect.ShowMessage(
                            throwable.message ?: if (isUpdate) "Gagal memperbarui wallet" else "Gagal membuat wallet"
                        )
                    )
                }

            _uiState.update { it.copy(isLoading = false) }
        }
    }
}

sealed interface UpsertWalletEffect {
    data class ShowMessage(val message: String) : UpsertWalletEffect
    data object NavigateBack : UpsertWalletEffect
}
