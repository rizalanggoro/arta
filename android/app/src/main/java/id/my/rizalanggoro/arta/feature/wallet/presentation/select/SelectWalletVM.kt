package id.my.rizalanggoro.arta.feature.wallet.presentation.select

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewModelScope
import id.my.rizalanggoro.arta.core.application.MyApplication
import id.my.rizalanggoro.arta.feature.wallet.data.WalletRepository
import id.my.rizalanggoro.arta.domain.Wallet
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SelectWalletVM(
    private val walletRepository: WalletRepository,
) : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as MyApplication)
                SelectWalletVM(walletRepository = app.walletRepository)
            }
        }
    }

    private val _uiState = MutableStateFlow(SelectWalletUiState())
    val uiState: StateFlow<SelectWalletUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<SelectWalletEffect>()
    val effect: SharedFlow<SelectWalletEffect> = _effect.asSharedFlow()

    fun loadWallets() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            walletRepository.getWallets()
                .onSuccess { wallets ->
                    _uiState.update { it.copy(wallets = wallets, isLoading = false) }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = throwable.message ?: "Gagal memuat wallet") }
                }
        }
    }

    fun selectWallet(wallet: Wallet) {
        viewModelScope.launch {
            WalletSelectionBus.emit(wallet)
            _effect.emit(SelectWalletEffect.NavigateBack)
        }
    }
}

sealed interface SelectWalletEffect {
    data object NavigateBack : SelectWalletEffect
}