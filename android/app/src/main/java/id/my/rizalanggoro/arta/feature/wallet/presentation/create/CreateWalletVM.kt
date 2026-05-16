package id.my.rizalanggoro.arta.feature.wallet.presentation.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateWalletVM : ViewModel() {
	private val _uiState = MutableStateFlow(CreateWalletUiState())
	val uiState: StateFlow<CreateWalletUiState> = _uiState.asStateFlow()

	private val _messageEvent = MutableSharedFlow<String>()
	val messageEvent: SharedFlow<String> = _messageEvent.asSharedFlow()

	fun onChangeName(value: String) {
		_uiState.update { it.copy(name = value, nameError = null) }
	}

	fun onChangeType(value: String) {
		_uiState.update { it.copy(type = value, typeError = null) }
	}

	fun onToggleDefault(value: Boolean) {
		_uiState.update { it.copy(isDefault = value) }
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
			_messageEvent.emit("Create wallet belum dihubungkan ke backend")
			_uiState.update { it.copy(isLoading = false) }
		}
	}
}