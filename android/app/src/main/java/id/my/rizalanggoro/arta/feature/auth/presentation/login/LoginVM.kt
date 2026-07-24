package id.my.rizalanggoro.arta.feature.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.rizalanggoro.arta.core.data.SelectedWalletPrefs
import id.my.rizalanggoro.arta.core.extension.errorMessage
import id.my.rizalanggoro.arta.feature.auth.domain.usecase.LoginUseCase
import id.my.rizalanggoro.arta.openapi.apis.WalletApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginVM @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val walletApi: WalletApi,
    private val selectedWalletPrefs: SelectedWalletPrefs,
) : ViewModel() {
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
                val session = loginUseCase(email = current.email, password = current.password)
                val authorization = "Bearer ${session.token}"
                val walletResponse = walletApi.listWallets(authorization)
                if (!walletResponse.isSuccessful) {
                    throw IllegalStateException(walletResponse.errorMessage())
                }

                walletResponse.body() ?: throw IllegalStateException("Respons server kosong")
            }.onSuccess { walletListResponse ->
                walletListResponse.wallets.firstOrNull()?.let { firstWallet ->
                    selectedWalletPrefs.saveSelectedWallet(firstWallet.data)
                }
                _event.emit(LoginUiState.Event.LoginSucceeded)
            }.onFailure { throwable ->
                throwable.printStackTrace()
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
