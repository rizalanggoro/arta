package id.my.rizalanggoro.arta.feature.wallet.presentation.update

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewModelScope
import id.my.rizalanggoro.arta.core.application.MyApplication
import id.my.rizalanggoro.arta.feature.wallet.data.WalletRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UpdateWalletVM(
	private val walletRepository: WalletRepository,
) : ViewModel() {
	companion object {
		val Factory = viewModelFactory {
			initializer {
				val walletRepository = (this[APPLICATION_KEY] as MyApplication).walletRepository
				UpdateWalletVM(walletRepository = walletRepository)
			}
		}
	}

	private val _uiState = MutableStateFlow(UpdateWalletUiState())
	val uiState: StateFlow<UpdateWalletUiState> = _uiState.asStateFlow()

	private val _effect = MutableSharedFlow<UpdateWalletEffect>()
	val effect: SharedFlow<UpdateWalletEffect> = _effect.asSharedFlow()

	fun loadWallet(walletId: Int) {
		viewModelScope.launch {
			_uiState.update { it.copy(walletId = walletId, isLoading = true, errorMessage = null) }
			walletRepository.getWalletById(walletId)
				.onSuccess { wallet ->
					_uiState.update {
						it.copy(
							walletId = wallet.id,
							name = wallet.name,
							type = wallet.type,
							isDefault = wallet.isDefault,
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

	fun onToggleDefault(value: Boolean) {
		_uiState.update { it.copy(isDefault = value) }
	}

	fun updateWallet() {
		val current = _uiState.value
		val walletId = current.walletId
		if (walletId == null) {
			viewModelScope.launch { _effect.emit(UpdateWalletEffect.ShowMessage("Wallet belum dimuat")) }
			return
		}

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
			viewModelScope.launch { _effect.emit(UpdateWalletEffect.ShowMessage("Periksa kembali isian wallet")) }
			return
		}

		viewModelScope.launch {
			_uiState.update { it.copy(isLoading = true) }
			walletRepository.updateWallet(
				id = walletId,
				name = current.name,
				type = current.type,
				isDefault = current.isDefault,
			)
				.onSuccess { wallet ->
					_effect.emit(UpdateWalletEffect.ShowMessage("Wallet ${wallet.name} berhasil diperbarui"))
					_effect.emit(UpdateWalletEffect.NavigateBack)
				}
				.onFailure { throwable ->
					_effect.emit(UpdateWalletEffect.ShowMessage(throwable.message ?: "Gagal memperbarui wallet"))
				}
			_uiState.update { it.copy(isLoading = false) }
		}
	}
}

sealed interface UpdateWalletEffect {
	data class ShowMessage(val message: String) : UpdateWalletEffect
	data object NavigateBack : UpdateWalletEffect
}
