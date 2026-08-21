package id.my.rizalanggoro.arta.feature.wallet.presentation.upsert

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.rizalanggoro.arta.core.data.AuthPrefs
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.core.extension.errorMessage
import id.my.rizalanggoro.arta.openapi.apis.WalletApi
import id.my.rizalanggoro.arta.openapi.models.WalletCreateWalletReq
import id.my.rizalanggoro.arta.openapi.models.WalletUpdateWalletReq
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class UpsertWalletVM @Inject constructor(
    private val walletApi: WalletApi,
    private val authPrefs: AuthPrefs,
) : ViewModel() {
    private var walletId: Int = 0

    // Reused across retries so the server can dedupe; rotated only when the
    // server definitively rejected a submission.
    private var idempotencyKey: String = UUID.randomUUID().toString()

    private val _uiState = MutableStateFlow(UpsertWalletUiState())
    val uiState: StateFlow<UpsertWalletUiState> = _uiState.asStateFlow()

    fun setWalletId(value: Int) {
        if (walletId == value) return
        walletId = value
        loadWallet()
    }

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
                        idempotencyKey = idempotencyKey,
                    )

                    if (!response.isSuccessful) {
                        throw IllegalStateException(response.errorMessage())
                    }

                    response.body() ?: throw IllegalStateException("Respons server kosong")
                }
            }

            result.onSuccess {
                AppEventBus.emit(AppEvent.WalletChanged)
            }.onFailure { throwable ->
                if (throwable is IllegalStateException) {
                    idempotencyKey = UUID.randomUUID().toString()
                }
            }

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    init {
        loadWallet()
    }

    private fun authorizationHeader(): String? {
        return authPrefs.currentSession.value?.token?.let { "Bearer $it" }
    }
}
