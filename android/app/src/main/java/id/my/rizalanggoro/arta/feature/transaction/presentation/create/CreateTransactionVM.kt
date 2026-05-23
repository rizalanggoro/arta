package id.my.rizalanggoro.arta.feature.transaction.presentation.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import id.my.rizalanggoro.arta.core.application.MyApplication
import id.my.rizalanggoro.arta.core.data.SelectedWalletPrefs
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.core.extension.toApiFormat
import id.my.rizalanggoro.arta.openapi.apis.TransactionApi
import id.my.rizalanggoro.arta.openapi.models.CreateTransactionReq
import kotlinx.serialization.json.Json
import id.my.rizalanggoro.arta.core.extension.errorMessage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal

class CreateTransactionVM(
    private val transactionApi: TransactionApi,
    private val selectedWalletPrefs: SelectedWalletPrefs,
    private val authSessionProvider: () -> String?,
) : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as MyApplication)
                CreateTransactionVM(
                    transactionApi = app.transactionApi,
                    selectedWalletPrefs = app.selectedWalletPrefs,
                    authSessionProvider = { app.authPrefs.currentSession.value?.token },
                )
            }
        }
    }

    private val _uiState = MutableStateFlow(CreateTransactionUiState())
    val uiState: StateFlow<CreateTransactionUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<CreateTransactionEvent>()
    val effect: SharedFlow<CreateTransactionEvent> = _effect.asSharedFlow()

    fun onAmountChanged(value: String) {
        _uiState.update { it.copy(amount = value, amountError = null) }
    }

    fun onDescriptionChanged(value: String) {
        _uiState.update { it.copy(description = value) }
    }

    fun onChangeDate(value: Long) = _uiState.update {
        it.copy(
            date = value,
            dateError = null,
            isDatePickerOpen = false
        )
    }

    fun onChangeDatePickerDialog(isOpen: Boolean) = _uiState.update {
        it.copy(isDatePickerOpen = isOpen)
    }

    fun createTransaction() {
        val current = _uiState.value
        var hasError = false

        val amount = current.amount.toDoubleOrNull() ?: 0.0
        if (amount <= 0) {
            _uiState.update { it.copy(amountError = "Nominal transaksi tidak valid!") }
            hasError = true
        }

        if (current.category == null) {
            _uiState.update { it.copy(categoryError = "Kategori tidak boleh kosong!") }
            hasError = true
        }

        if (hasError) {
            viewModelScope.launch { _effect.emit(CreateTransactionEvent.ShowMessage("Periksa kembali isian transaksi")) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            runCatching {
                val authorization = authorizationHeader()
                    ?: throw IllegalStateException("Sesi login tidak ditemukan")

                val response = transactionApi.createTransaction(
                    authorization = authorization,
                    body = CreateTransactionReq(
                        amount = BigDecimal(amount),
                        categoryId = current.category!!.id,
                        date = current.date.toApiFormat(),
                        walletId = requireNotNull(current.wallet?.id) { "Wallet response missing id" },
                        description = current.description.ifBlank { null },
                    ),
                )

                if (!response.isSuccessful) {
                    val j = Json { ignoreUnknownKeys = true }
                    throw IllegalStateException(response.errorMessage(j))
                }

                response.body() ?: throw IllegalStateException("Respons server kosong")
            }.onSuccess {
                _effect.emit(CreateTransactionEvent.Success)
                AppEventBus.emit(AppEvent.TransactionChanged)
            }.onFailure { throwable ->
                _effect.emit(
                    CreateTransactionEvent.ShowMessage(
                        throwable.message ?: "Terjadi kesalahan tak terduga!"
                    )
                )
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun authorizationHeader(): String? {
        return authSessionProvider()?.let { "Bearer $it" }
    }

    init {
        viewModelScope.launch {
            selectedWalletPrefs.selectedWallet.collect { wallet ->
                _uiState.update {
                    it.copy(wallet = wallet)
                }
            }
        }

        viewModelScope.launch {
            AppEventBus.event
                .filterIsInstance<AppEvent.CategorySelected>()
                .collect { event ->
                    _uiState.update {
                        it.copy(
                            category = event.category,
                            categoryError = null
                        )
                    }
                }
        }
    }
}

sealed interface CreateTransactionEvent {
    data class ShowMessage(val message: String) : CreateTransactionEvent
    data object Success : CreateTransactionEvent
}
