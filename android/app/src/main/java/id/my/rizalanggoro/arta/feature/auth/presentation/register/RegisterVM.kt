package id.my.rizalanggoro.arta.feature.auth.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.rizalanggoro.arta.feature.auth.domain.usecase.RegisterUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterVM @Inject constructor(
    private val registerUseCase: RegisterUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<RegisterUiState.Event>()
    val event = _event.asSharedFlow()

    fun onNameChanged(value: String) {
        _uiState.update { it.copy(name = value, nameError = null) }
    }

    fun onEmailChanged(value: String) {
        _uiState.update { it.copy(email = value, emailError = null) }
    }

    fun onPasswordChanged(value: String) {
        _uiState.update { it.copy(password = value, passwordError = null) }
    }

    fun onConfirmPasswordChanged(value: String) {
        _uiState.update { it.copy(confirmPassword = value, confirmPasswordError = null) }
    }

    fun onRegisterClicked() {
        val current = _uiState.value
        var hasError = false

        if (current.name.isBlank()) {
            _uiState.update { it.copy(nameError = "Nama wajib diisi") }
            hasError = true
        }

        if (current.email.isBlank()) {
            _uiState.update { it.copy(emailError = "Email wajib diisi") }
            hasError = true
        }

        if (current.password.isBlank()) {
            _uiState.update { it.copy(passwordError = "Kata sandi wajib diisi") }
            hasError = true
        }

        if (current.confirmPassword.isBlank()) {
            _uiState.update { it.copy(confirmPasswordError = "Konfirmasi kata sandi wajib diisi") }
            hasError = true
        }

        if (current.password.isNotBlank() && current.confirmPassword.isNotBlank() && current.password != current.confirmPassword) {
            _uiState.update { it.copy(confirmPasswordError = "Konfirmasi kata sandi tidak sama") }
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            runCatching {
                registerUseCase(
                    name = current.name,
                    email = current.email,
                    password = current.password,
                )
            }.onSuccess {
                _event.emit(RegisterUiState.Event.RegisterSucceeded)
            }.onFailure { throwable ->
                _event.emit(
                    RegisterUiState.Event.ShowMessage(
                        message = throwable.message ?: "Terjadi kesalahan tak terduga"
                    )
                )
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }
}