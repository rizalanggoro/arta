package id.my.rizalanggoro.arta.feature.auth.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import id.my.rizalanggoro.arta.core.application.MyApplication
import id.my.rizalanggoro.arta.core.data.AuthPrefs
import id.my.rizalanggoro.arta.core.network.RetrofitProvider
import id.my.rizalanggoro.arta.domain.AuthSession
import id.my.rizalanggoro.arta.openapi.apis.AuthApi
import id.my.rizalanggoro.arta.openapi.models.RegisterReq
import id.my.rizalanggoro.arta.openapi.models.RegisterRes
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterVM(
    private val authApi: AuthApi,
    private val authPrefs: AuthPrefs,
) : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as MyApplication)
                RegisterVM(
                    authApi = RetrofitProvider.create(AuthApi::class.java),
                    authPrefs = app.authPrefs,
                )
            }
        }
    }

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private val _messageEvent = MutableSharedFlow<String>()
    val messageEvent: SharedFlow<String> = _messageEvent.asSharedFlow()

    private val _effect = MutableSharedFlow<RegisterEffect>()
    val effect: SharedFlow<RegisterEffect> = _effect.asSharedFlow()

    fun onChangeName(value: String) {
        _uiState.update { it.copy(name = value, nameError = null) }
    }

    fun onChangeEmail(value: String) {
        _uiState.update { it.copy(email = value, emailError = null) }
    }

    fun onChangePassword(value: String) {
        _uiState.update { it.copy(password = value, passwordError = null) }
    }

    fun onChangeConfirmPassword(value: String) {
        _uiState.update { it.copy(confirmPassword = value, confirmPasswordError = null) }
    }

    fun register() {
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

        if (hasError) {
            viewModelScope.launch {
                _messageEvent.emit("Periksa kembali isian registrasi")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            runCatching {
                val response = authApi.apiAuthRegisterPost(
                    RegisterReq(
                        name = current.name,
                        email = current.email,
                        password = current.password,
                    ),
                )

                if (!response.isSuccessful) {
                    throw IllegalStateException("Registrasi gagal")
                }

                val body = response.body() ?: throw IllegalStateException("Respons server kosong")
                body.toDomain()
            }
                .onSuccess { session ->
                    authPrefs.setSession(session)
                    _messageEvent.emit("Registrasi berhasil untuk ${session.name}")
                    _effect.emit(RegisterEffect.NavigateToCreateFirstWallet)
                }
                .onFailure { throwable ->
                    _messageEvent.emit(throwable.message ?: "Registrasi gagal")
                }
            _uiState.update { it.copy(isLoading = false) }
        }
    }
}

sealed class RegisterEffect {
    object NavigateToCreateFirstWallet : RegisterEffect()
}

private fun RegisterRes.toDomain(): AuthSession {
    return AuthSession(
        userId = requireNotNull(userId) { "Register response missing user id" },
        email = requireNotNull(email) { "Register response missing email" },
        name = requireNotNull(name) { "Register response missing name" },
        token = requireNotNull(token) { "Register response missing token" },
    )
}