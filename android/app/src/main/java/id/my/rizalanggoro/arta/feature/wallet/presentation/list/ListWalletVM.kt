package id.my.rizalanggoro.arta.feature.wallet.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import id.my.rizalanggoro.arta.core.application.MyApplication
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import kotlinx.serialization.json.Json
import id.my.rizalanggoro.arta.core.extension.errorMessage
import id.my.rizalanggoro.arta.openapi.apis.WalletApi
import id.my.rizalanggoro.arta.openapi.models.DtoWallet
import id.my.rizalanggoro.arta.openapi.models.DomainWallet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ListWalletVM(
    private val walletApi: WalletApi,
) : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            initializer {
                val walletApi = (this[APPLICATION_KEY] as MyApplication).walletApi
                ListWalletVM(walletApi = walletApi)
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
                val response = walletApi.listWallets()
                if (!response.isSuccessful) {
                    val j = Json { ignoreUnknownKeys = true }
                    throw IllegalStateException(response.errorMessage(j))
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

    fun onDeleteRequested(wallet: DomainWallet) {
        _uiState.update { it.copy(deleteTarget = wallet, selectedWallet = null) }
    }

    fun dismissDeleteDialog() {
        _uiState.update { it.copy(deleteTarget = null) }
    }

    fun onWalletSelected(wallet: DomainWallet) {
        _uiState.update { it.copy(selectedWallet = wallet) }
    }

    fun dismissWalletActions() {
        _uiState.update { it.copy(selectedWallet = null) }
    }

    fun confirmDeleteWallet(wallet: DomainWallet) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching {
                val response = walletApi.deleteWallet(requireNotNull(wallet.id) {
                    "Wallet response missing id"
                })
                if (!response.isSuccessful) {
                    val j = Json { ignoreUnknownKeys = true }
                    throw IllegalStateException(response.errorMessage(j))
                }
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        deleteTarget = null
                    )
                }
                loadWallets()
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Gagal menghapus wallet",
                    )
                }
            }
        }

        viewModelScope.launch {
            AppEventBus.event
                .filterIsInstance<AppEvent.WalletChanged>()
                .collect { loadWallets() }
        }
    }

    init {
        loadWallets()
    }
}
