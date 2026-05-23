package id.my.rizalanggoro.arta.feature.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import id.my.rizalanggoro.arta.core.application.MyApplication
import id.my.rizalanggoro.arta.core.data.AuthPrefs
import id.my.rizalanggoro.arta.core.data.SelectedWalletPrefs
import id.my.rizalanggoro.arta.core.extension.errorMessage
import id.my.rizalanggoro.arta.domain.AuthSession
import id.my.rizalanggoro.arta.openapi.apis.AuthApi
import id.my.rizalanggoro.arta.openapi.apis.WalletApi
import id.my.rizalanggoro.arta.openapi.models.LoginReq
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginVM(
    private val authApi: AuthApi,
    private val authPrefs: AuthPrefs,
    private val walletApi: WalletApi,
    private val selectedWalletPrefs: SelectedWalletPrefs,
) : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as MyApplication)
                LoginVM(
                    authApi = app.authApi,
                    authPrefs = app.authPrefs,
                    walletApi = app.walletApi,
                    selectedWalletPrefs = app.selectedWalletPrefs,
                )
            }
        }
    }

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<LoginUiState.Event>()
    val event = _event.asSharedFlow()

    fun onEmailChanged(value: String) {
        _uiState.update { it.copy(email = value, emailError = null) }
    }

    fun onPasswordChanged(value: String) {
        _uiState.update { it.copy(password = value, passwordError = null) }
    }

    fun onLoginClicked() {
        val current = _uiState.value
        var hasError = false

        if (current.email.isBlank()) {
            _uiState.update { it.copy(emailError = "Alamat email tidak boleh kosong!") }
            hasError = true
        }

        if (current.password.isBlank()) {
            _uiState.update { it.copy(passwordError = "Kata sandi tidak boleh kosong!") }
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            runCatching {
                val response = authApi.apiAuthLoginPost(
                    LoginReq(
                        email = current.email,
                        password = current.password,
                    ),
                )

                if (!response.isSuccessful) {
                    throw IllegalStateException(response.errorMessage())
                }

                response.body() ?: throw IllegalStateException("Respons server kosong")
            }.onSuccess { loginResponse ->
                authPrefs.setSession(
                    session = AuthSession(
                        userId = requireNotNull(loginResponse.userId) { "Login response missing user id" },
                        email = requireNotNull(loginResponse.email) { "Login response missing email" },
                        name = requireNotNull(loginResponse.name) { "Login response missing name" },
                        token = requireNotNull(loginResponse.token) { "Login response missing token" },
                    )
                )

                runCatching {
                    val authorization = "Bearer ${loginResponse.token}"
                    val walletResponse = walletApi.listWallets(authorization)
                    if (!walletResponse.isSuccessful) {
                        throw IllegalStateException(walletResponse.errorMessage())
                    }

                    walletResponse.body() ?: throw IllegalStateException("Respons server kosong")
                }.onSuccess { walletListResponse ->
                    walletListResponse.wallets.firstOrNull()?.let { firstWallet ->
                        selectedWalletPrefs.saveSelectedWallet(firstWallet.data)
                    }
                }
                _event.emit(LoginUiState.Event.LoginSucceeded)
            }.onFailure { throwable ->
                _event.emit(
                    LoginUiState.Event.ShowMessage(
                        message = throwable.message ?: "Terjadi kesalahan tak terduga"
                    )
                )
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }
}
