package id.my.rizalanggoro.arta.feature.wallet.presentation.list

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
import id.my.rizalanggoro.arta.openapi.models.DomainWallet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ListWalletVM(
    private val walletApi: WalletApi,
    private val authSessionProvider: () -> AuthSession?,
) : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as MyApplication
                ListWalletVM(
                    walletApi = app.walletApi,
                    authSessionProvider = { app.authPrefs.currentSession.value },
                )
            }
        }
    }

    private val _uiState = MutableStateFlow(ListWalletUiState())
    val uiState: StateFlow<ListWalletUiState> = _uiState.asStateFlow()

    fun loadWallets() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }
            runCatching {
                val response = walletApi.listWallets(authorizationHeader() ?: throw IllegalStateException("Sesi login tidak ditemukan"))
                if (!response.isSuccessful) {
                    throw IllegalStateException(response.errorMessage())
                }

                response.body() ?: throw IllegalStateException("Response body is null")
            }.onSuccess { response ->
                _uiState.update {
                    it.copy(
                        wallets = response.wallets,
                        isLoading = false,
                        errorMessage = null,
                        deleteTarget = null,
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

    fun onWalletSelected(wallet: DomainWallet) = _uiState.update {
        it.copy(
            selectedWallet = wallet
        )
    }

    fun onActionDeleteClicked() = _uiState.update {
        it.copy(
            deleteTarget = it.selectedWallet,
            selectedWallet = null
        )
    }

    fun onActionDismissed() = _uiState.update {
        it.copy(
            selectedWallet = null,
            deleteTarget = null
        )
    }

    fun onDialogDismissed() = _uiState.update {
        it.copy(deleteTarget = null)
    }

    fun confirmDeleteWallet() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isDeleting = true
                )
            }
            runCatching {
                val response =
                    walletApi.deleteWallet(
                        authorizationHeader() ?: throw IllegalStateException("Sesi login tidak ditemukan"),
                        requireNotNull(_uiState.value.deleteTarget?.id) {
                            "Wallet response missing id"
                        }
                    )
                if (!response.isSuccessful) {
                        throw IllegalStateException(response.errorMessage())
                }
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        isDeleting = false,
                        deleteTarget = null
                    )
                }
                loadWallets()
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isDeleting = false,
                        // errorMessage = throwable.message ?: "Gagal menghapus wallet",
                    )
                }
            }
        }
    }

    init {
        loadWallets()

        viewModelScope.launch {
            AppEventBus.event
                .filterIsInstance<AppEvent.WalletChanged>()
                .collect { loadWallets() }
        }
    }

    private fun authorizationHeader(): String? {
        return authSessionProvider()?.token?.let { "Bearer $it" }
    }
}
