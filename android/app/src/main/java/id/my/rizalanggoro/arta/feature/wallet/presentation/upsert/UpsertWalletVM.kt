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
import id.my.rizalanggoro.arta.domain.AuthSession
import id.my.rizalanggoro.arta.openapi.apis.WalletApi
import id.my.rizalanggoro.arta.openapi.models.WalletCreateWalletReq
import id.my.rizalanggoro.arta.openapi.models.WalletUpdateWalletReq
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UpsertWalletVM(
    private val walletApi: WalletApi,
    private val walletId: Int,
    private val authSessionProvider: () -> AuthSession?,
) : ViewModel() {
    companion object {
        fun Factory(walletId: Int) = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as MyApplication)
                UpsertWalletVM(
                    walletApi = app.walletApi,
                    walletId = walletId,
                    authSessionProvider = { app.authPrefs.currentSession.value },
                )
            }
        }
    }

    private val _uiState = MutableStateFlow(UpsertWalletUiState())
    val uiState: StateFlow<UpsertWalletUiState> = _uiState.asStateFlow()

    fun loadWallet() {
        if (walletId == 0) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    walletId = walletId,
                    isLoading = true,
                    errorMessage = null,
                )
            }

            runCatching {
                val authorization = authorizationHeader()
                    ?: throw IllegalStateException("Sesi login tidak ditemukan")

                val response = walletApi.getWallet(authorization, walletId)
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

    fun onNameChanged(value: String) = _uiState.update {
        it.copy(
            name = value,
            nameError = null
        )
    }

    fun onTypeChanged(value: String) = _uiState.update {
        it.copy(
            type = value,
        )
    }

    fun submit() {
        val current = _uiState.value
        val isUpdate = current.walletId > 0

        var hasError = false
        if (current.name.isBlank()) {
            _uiState.update { it.copy(nameError = "Nama dompet tidak boleh kosong!") }
            hasError = true
        }
        if (hasError) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = if (isUpdate) {
                runCatching {
                    val authorization = authorizationHeader()
                        ?: throw IllegalStateException("Sesi login tidak ditemukan")

                    val response = walletApi.updateWallet(
                        authorization = authorization,
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
                    val authorization = authorizationHeader()
                        ?: throw IllegalStateException("Sesi login tidak ditemukan")

                    val response = walletApi.createWallet(
                        authorization = authorization,
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

            result.onSuccess {
                AppEventBus.emit(AppEvent.WalletChanged)
            }

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    init {
        loadWallet()
    }

    private fun authorizationHeader(): String? {
        return authSessionProvider()?.token?.let { "Bearer $it" }
    }
}
