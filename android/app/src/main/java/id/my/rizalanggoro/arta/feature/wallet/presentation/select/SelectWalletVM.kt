package id.my.rizalanggoro.arta.feature.wallet.presentation.select

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import id.my.rizalanggoro.arta.core.application.MyApplication
import id.my.rizalanggoro.arta.core.data.SelectedWalletPrefs
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.domain.Wallet
import id.my.rizalanggoro.arta.feature.wallet.data.WalletRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SelectWalletVM(
    private val walletRepository: WalletRepository,
    private val selectedWalletPrefs: SelectedWalletPrefs,
) : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as MyApplication)
                SelectWalletVM(
                    walletRepository = app.walletRepository,
                    selectedWalletPrefs = app.selectedWalletPrefs
                )
            }
        }
    }

    private val _uiState = MutableStateFlow(SelectWalletUiState())
    val uiState: StateFlow<SelectWalletUiState> = _uiState.asStateFlow()

    fun loadWallets() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            walletRepository.getWallets()
                .onSuccess { wallets ->
                    _uiState.update { it.copy(wallets = wallets, isLoading = false) }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.message ?: "Gagal memuat wallet"
                        )
                    }
                }
        }
    }

    fun selectWallet(wallet: Wallet) {
        viewModelScope.launch {
            selectedWalletPrefs.saveSelectedWallet(wallet)
            AppEventBus.emit(
                AppEvent.WalletSelected(
                    wallet = wallet
                )
            )
        }
    }

    init {
        loadWallets()

        viewModelScope.launch {
            selectedWalletPrefs.selectedWallet.collect { wallet ->
                _uiState.update { it.copy(selectedWallet = wallet) }
            }
        }

        viewModelScope.launch {
            AppEventBus.event
                .filterIsInstance<AppEvent.WalletChanged>()
                .collect { loadWallets() }
        }
    }
}