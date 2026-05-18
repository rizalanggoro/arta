package id.my.rizalanggoro.arta.feature.wallet.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewModelScope
import id.my.rizalanggoro.arta.core.application.MyApplication
import id.my.rizalanggoro.arta.domain.Wallet
import id.my.rizalanggoro.arta.feature.wallet.data.WalletRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ListWalletVM(
	private val walletRepository: WalletRepository,
) : ViewModel() {
	companion object {
		val Factory = viewModelFactory {
			initializer {
				val walletRepository = (this[APPLICATION_KEY] as MyApplication).walletRepository
				ListWalletVM(walletRepository = walletRepository)
			}
		}
	}

	private val _uiState = MutableStateFlow(ListWalletUiState())
	val uiState: StateFlow<ListWalletUiState> = _uiState.asStateFlow()

	fun loadWallets() {
		viewModelScope.launch {
			_uiState.update { it.copy(isLoading = true, errorMessage = null) }
			walletRepository.getWallets()
				.onSuccess { wallets ->
					_uiState.update {
						it.copy(
							wallets = wallets,
							isLoading = false,
							errorMessage = null,
							deleteTarget = null,
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

	fun onDeleteRequested(wallet: Wallet) {
		_uiState.update { it.copy(deleteTarget = wallet, selectedWallet = null) }
	}

	fun dismissDeleteDialog() {
		_uiState.update { it.copy(deleteTarget = null) }
	}

	fun onWalletSelected(wallet: Wallet) {
		_uiState.update { it.copy(selectedWallet = wallet) }
	}

	fun dismissWalletActions() {
		_uiState.update { it.copy(selectedWallet = null) }
	}

	fun confirmDeleteWallet(wallet: Wallet) {
		viewModelScope.launch {
			_uiState.update { it.copy(isLoading = true, errorMessage = null) }
			walletRepository.deleteWallet(wallet.id)
				.onSuccess {
					_uiState.update { it.copy(isLoading = false, deleteTarget = null) }
					loadWallets()
				}
				.onFailure { throwable ->
					_uiState.update {
						it.copy(
							isLoading = false,
							errorMessage = throwable.message ?: "Gagal menghapus wallet",
						)
					}
				}
		}
	}
}
