package id.my.rizalanggoro.arta.feature.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewModelScope
import id.my.rizalanggoro.arta.core.application.MyApplication
import id.my.rizalanggoro.arta.feature.auth.data.AuthRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginVM(
	private val authRepository: AuthRepository,
	private val application: MyApplication,
) : ViewModel() {
	companion object {
		val Factory = viewModelFactory {
			initializer {
				val app = (this[APPLICATION_KEY] as MyApplication)
				LoginVM(
					authRepository = app.authRepository,
					application = app,
				)
			}
		}
	}

	private val _uiState = MutableStateFlow(LoginUiState())
	val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

	private val _messageEvent = MutableSharedFlow<String>()
	val messageEvent: SharedFlow<String> = _messageEvent.asSharedFlow()

	fun onChangeEmail(value: String) {
		_uiState.update { it.copy(email = value, emailError = null) }
	}

	fun onChangePassword(value: String) {
		_uiState.update { it.copy(password = value, passwordError = null) }
	}

	fun login() {
		val current = _uiState.value
		var hasError = false

		if (current.email.isBlank()) {
			_uiState.update { it.copy(emailError = "Email wajib diisi") }
			hasError = true
		}

		if (current.password.isBlank()) {
			_uiState.update { it.copy(passwordError = "Kata sandi wajib diisi") }
			hasError = true
		}

		if (hasError) {
			viewModelScope.launch {
				_messageEvent.emit("Periksa kembali isian login")
			}
			return
		}

		viewModelScope.launch {
			_uiState.update { it.copy(isLoading = true) }
                authRepository.login(current.email, current.password)
                	.onSuccess { session ->
					application.authPrefs.setSession(session)
					_messageEvent.emit("Login berhasil untuk ${session.name}")
					// Save first wallet as selected wallet if available
					val walletsResult = application.walletRepository.getWallets()
					walletsResult.onSuccess { wallets ->
						if (wallets.isNotEmpty()) {
							application.selectedWalletPrefs.saveSelectedWalletId(wallets.first().id)
						}
					}
                	}
				.onFailure { throwable ->
					_messageEvent.emit(throwable.message ?: "Login gagal")
				}
			_uiState.update { it.copy(isLoading = false) }
		}
	}
}