package id.my.rizalanggoro.arta.feature.wallet.presentation.select

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.rizalanggoro.arta.core.data.AuthPrefs
import id.my.rizalanggoro.arta.core.data.SelectedWalletPrefs
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.core.extension.errorMessage
import id.my.rizalanggoro.arta.openapi.apis.WalletApi
import id.my.rizalanggoro.arta.openapi.models.DomainWallet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectWalletVM @Inject constructor(
    private val walletApi: WalletApi,
    private val selectedWalletPrefs: SelectedWalletPrefs,
    private val authPrefs: AuthPrefs,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SelectWalletUiState())
    val uiState: StateFlow<SelectWalletUiState> = _uiState.asStateFlow()

    fun loadWallets() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        runCatching {
            val authorization = authorizationHeader()
                ?: throw IllegalStateException("Sesi login tidak ditemukan")

            val response = walletApi.listWallets(authorization)
            if (!response.isSuccessful) {
                throw IllegalStateException(response.errorMessage())
            }

            response.body() ?: throw IllegalStateException("Respons server kosong")
        }.onSuccess { response ->
            _uiState.update {
                it.copy(
                    wallets = response.wallets,
                    isLoading = false
                )
            }
        }.onFailure { throwable ->
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = throwable.message ?: "Terjadi kesalahan tak terduga"
                )
            }
        }
    }

    private fun authorizationHeader(): String? {
        return authPrefs.currentSession.value?.token?.let { "Bearer $it" }
    }

    fun onWalletSelected(wallet: DomainWallet) = viewModelScope.launch {
        selectedWalletPrefs.saveSelectedWallet(wallet)
        AppEventBus.emit(
            AppEvent.WalletSelected(
                wallet = wallet
            )
        )
    }

    init {
        loadWallets()

        viewModelScope.launch {
            selectedWalletPrefs.selectedWallet.collect { wallet ->
                _uiState.update {
                    it.copy(
                        selectedWallet = wallet
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
}