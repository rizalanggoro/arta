package id.my.rizalanggoro.arta.feature.wallet.presentation.create

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

class CreateWalletVM(
	private val walletRepository: WalletRepository,
	private val application: MyApplication,
) : ViewModel() {
	companion object {
		val Factory = viewModelFactory {
			initializer {
				val app = (this[APPLICATION_KEY] as MyApplication)
				CreateWalletVM(
					walletRepository = app.walletRepository,
					application = app,
				)
			}
		}
	}

	private val _uiState = MutableStateFlow(CreateWalletUiState())
	val uiState: StateFlow<CreateWalletUiState> = _uiState.asStateFlow()

	private val _messageEvent = MutableSharedFlow<String>()
	val messageEvent: SharedFlow<String> = _messageEvent.asSharedFlow()

	private val _effect = MutableSharedFlow<CreateWalletEffect>()
	val effect: SharedFlow<CreateWalletEffect> = _effect.asSharedFlow()

	fun onChangeName(value: String) {
		_uiState.update { it.copy(name = value, nameError = null) }
	}

	fun onChangeType(value: String) {
		_uiState.update { it.copy(type = value, typeError = null) }
	}

	fun onToggleDefault(value: Boolean) {
		// isDefault removed; no-op
	}

	fun create() {
		val current = _uiState.value
		var hasError = false

		if (current.name.isBlank()) {
			_uiState.update { it.copy(nameError = "Nama wajib diisi") }
			hasError = true
		}

		if (current.type.isBlank()) {
			_uiState.update { it.copy(typeError = "Tipe wajib diisi") }
			hasError = true
		}

		if (hasError) {
			viewModelScope.launch { _messageEvent.emit("Periksa kembali isian wallet") }
			return
		}

		viewModelScope.launch {
			_uiState.update { it.copy(isLoading = true) }
			val result = walletRepository.createWallet(name = current.name, type = current.type)
			result
				.onSuccess { wallet ->
					application.selectedWalletPrefs.saveSelectedWalletId(wallet.id)
					_messageEvent.emit("Wallet dibuat: ${wallet.name}")
					_effect.emit(CreateWalletEffect.NavigateBack)
				}
				.onFailure { t ->
					_messageEvent.emit(t.message ?: "Gagal membuat wallet")
				}
			_uiState.update { it.copy(isLoading = false) }
		}
	}
}

sealed class CreateWalletEffect {
	object NavigateBack : CreateWalletEffect()
}