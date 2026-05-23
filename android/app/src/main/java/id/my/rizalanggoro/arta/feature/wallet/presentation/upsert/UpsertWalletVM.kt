package id.my.rizalanggoro.arta.feature.wallet.presentation.upsert

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import id.my.rizalanggoro.arta.core.application.MyApplication
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.core.extension.errorMessage
import id.my.rizalanggoro.arta.openapi.apis.WalletApi
import id.my.rizalanggoro.arta.openapi.models.CreateWalletRes
import id.my.rizalanggoro.arta.openapi.models.UpdateWalletRes
import id.my.rizalanggoro.arta.openapi.models.WalletCreateWalletReq
import id.my.rizalanggoro.arta.openapi.models.WalletUpdateWalletReq
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UpsertWalletVM(
    private val walletApi: WalletApi,
) : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as MyApplication)
                UpsertWalletVM(walletApi = app.walletApi)
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

            runCatching {
                val response = walletApi.getWallet(walletId)
                if (!response.isSuccessful) {
                    throw IllegalStateException(response.errorMessage())
                }

                response.body() ?: throw IllegalStateException("Respons server kosong")
            }.onSuccess { response ->
                _uiState.update {
                    it.copy(
                        walletId = requireNotNull(response.data.id) { "Wallet response missing id" },
                        name = requireNotNull(response.data.name) { "Wallet response missing name" },
                        type = requireNotNull(response.data.type) { "Wallet response missing type" },
                        isLoading = false,
                        errorMessage = null,
                    )
                }
            }.onFailure { throwable ->
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
                runCatching {
                    val response = walletApi.updateWallet(
                        id = current.walletId,
                        body = WalletUpdateWalletReq(
                            name = current.name,
                            type = current.type,
                        ),
                    )

                    if (!response.isSuccessful) {
                        throw IllegalStateException(response.errorMessage())
                    }

                    response.body() ?: throw IllegalStateException("Respons server kosong")
                }
            } else {
                runCatching {
                    val response = walletApi.createWallet(
                        WalletCreateWalletReq(
                            name = current.name,
                            type = current.type,
                        ),
                    )

                    if (!response.isSuccessful) {
                        throw IllegalStateException(response.errorMessage())
                    }

                    response.body() ?: throw IllegalStateException("Respons server kosong")
                }
            }

            result
                .onSuccess { response ->
                    val walletName = when (response) {
                        is CreateWalletRes -> response.data.name.orEmpty()
                        is UpdateWalletRes -> response.data.name.orEmpty()
                        else -> ""
                    }

                    AppEventBus.emit(AppEvent.WalletChanged)
                    _effect.emit(
                        UpsertWalletEffect.ShowMessage(
                            if (isUpdate) {
                                "Wallet $walletName berhasil diperbarui"
                            } else {
                                "Wallet $walletName berhasil dibuat"
                            }
                        )
                    )
                    _effect.emit(UpsertWalletEffect.NavigateBack)
                }
                .onFailure { throwable ->
                    _effect.emit(
                        UpsertWalletEffect.ShowMessage(
                            throwable.message
                                ?: if (isUpdate) "Gagal memperbarui wallet" else "Gagal membuat wallet"
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
