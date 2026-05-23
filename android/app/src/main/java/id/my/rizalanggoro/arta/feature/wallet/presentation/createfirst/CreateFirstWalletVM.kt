package id.my.rizalanggoro.arta.feature.wallet.presentation.createfirst

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.rizalanggoro.arta.core.data.SelectedWalletPrefs
import id.my.rizalanggoro.arta.core.data.AuthPrefs
import id.my.rizalanggoro.arta.core.extension.errorMessage
import id.my.rizalanggoro.arta.openapi.apis.WalletApi
import id.my.rizalanggoro.arta.openapi.models.WalletCreateWalletReq
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateFirstWalletVM @Inject constructor(
    private val walletApi: WalletApi,
    private val selectedWalletPrefs: SelectedWalletPrefs,
    private val authPrefs: AuthPrefs,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CreateFirstWalletUiState())
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<CreateFirstWalletUiState.Event>()
    val event = _event.asSharedFlow()

    fun onChangeName(value: String) {
        _uiState.update { it.copy(name = value, nameError = null) }
    }

    fun onChangeType(value: String) {
        _uiState.update { it.copy(type = value, typeError = null) }
    }

    fun create() {
        val current = _uiState.value
        var hasError = false

        if (current.name.isBlank()) {
            _uiState.update { it.copy(nameError = "Nama dompet tidak boleh kosong!") }
            hasError = true
        }

        if (current.type.isBlank()) {
            _uiState.update { it.copy(typeError = "Tipe dompet tidak boleh kosong!") }
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            runCatching {
                val authorization = authorizationHeader()
                    ?: throw IllegalStateException("Sesi login tidak ditemukan")

                val response = walletApi.createWallet(
                    authorization,
                    WalletCreateWalletReq(
                        name = current.name,
                        type = current.type,
                    )
                )

                if (!response.isSuccessful) {
                    throw IllegalStateException(response.errorMessage())
                }

                response.body() ?: throw IllegalStateException("Respons server kosong")
            }.onSuccess { response ->
                selectedWalletPrefs.saveSelectedWallet(response.data)
                _event.emit(CreateFirstWalletUiState.Event.CreateSucceeded)
            }.onFailure { throwable ->
                _event.emit(
                    CreateFirstWalletUiState.Event.ShowMessage(
                        message = throwable.message ?: "Terjadi kesalahan tak terduga"
                    )
                )
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun authorizationHeader(): String? {
        return authPrefs.currentSession.value?.token?.let { "Bearer $it" }
    }
}