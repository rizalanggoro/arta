package id.my.rizalanggoro.arta.feature.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import id.my.rizalanggoro.arta.core.application.MyApplication
import id.my.rizalanggoro.arta.core.data.AuthPrefs
import id.my.rizalanggoro.arta.core.data.SelectedWalletPrefs
import id.my.rizalanggoro.arta.core.network.RetrofitProvider
import id.my.rizalanggoro.arta.domain.AuthSession
import id.my.rizalanggoro.arta.feature.wallet.walletApiErrorMessage
import id.my.rizalanggoro.arta.openapi.apis.AuthApi
import id.my.rizalanggoro.arta.openapi.apis.WalletApi
import id.my.rizalanggoro.arta.openapi.models.DtoError
import id.my.rizalanggoro.arta.openapi.models.LoginReq
import id.my.rizalanggoro.arta.openapi.models.LoginRes
import kotlinx.serialization.json.Json
import retrofit2.Response
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
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
    private val errorJson = Json {
        ignoreUnknownKeys = true
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as MyApplication)
                LoginVM(
                    authApi = RetrofitProvider.create(AuthApi::class.java),
                    authPrefs = app.authPrefs,
                    walletApi = app.walletApi,
                    selectedWalletPrefs = app.selectedWalletPrefs,
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

            runCatching {
                val response = authApi.apiAuthLoginPost(
                    LoginReq(
                        email = current.email,
                        password = current.password,
                    ),
                )

                if (!response.isSuccessful) {
                    throw IllegalStateException(apiErrorMessage(response))
                }

                val body = response.body() ?: throw IllegalStateException("Respons server kosong")
                body.toDomain()
            }
                .onSuccess { session ->
                    authPrefs.setSession(session)
                    runCatching {
                        val response = walletApi.listWallets()
                        if (!response.isSuccessful) {
                            throw IllegalStateException(response.walletApiErrorMessage())
                        }

                        response.body()?.wallets.orEmpty().mapNotNull { it.data }
                    }
                        .onSuccess { wallets ->
                            wallets.firstOrNull()?.let { firstWallet ->
                                selectedWalletPrefs.saveSelectedWallet(firstWallet)
                            }
                        }
                    _messageEvent.emit("Login berhasil untuk ${session.name}")
                }
                .onFailure { throwable ->
                    _messageEvent.emit(throwable.message ?: "Login gagal")
                }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun apiErrorMessage(response: Response<LoginRes>): String {
        val rawError = response.errorBody()?.string().orEmpty()
        if (rawError.isBlank()) {
            return "Login gagal"
        }

        return runCatching {
            val error = errorJson.decodeFromString(DtoError.serializer(), rawError)
            error.message?.takeIf { it.isNotBlank() } ?: rawError
        }.getOrElse {
            rawError
        }
    }
}

private fun LoginRes.toDomain(): AuthSession {
    return AuthSession(
        userId = requireNotNull(userId) { "Login response missing user id" },
        email = requireNotNull(email) { "Login response missing email" },
        name = requireNotNull(name) { "Login response missing name" },
        token = requireNotNull(token) { "Login response missing token" },
    )
}