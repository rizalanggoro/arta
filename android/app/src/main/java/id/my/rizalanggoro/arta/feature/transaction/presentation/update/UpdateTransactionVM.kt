package id.my.rizalanggoro.arta.feature.transaction.presentation.update

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import id.my.rizalanggoro.arta.core.application.MyApplication
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import kotlinx.serialization.json.Json
import id.my.rizalanggoro.arta.core.extension.errorMessage
import id.my.rizalanggoro.arta.openapi.apis.TransactionApi
import id.my.rizalanggoro.arta.openapi.models.UpdateTransactionReq
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

class UpdateTransactionVM(
    private val transactionApi: TransactionApi,
    private val authSessionProvider: () -> String?,
) : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as MyApplication)
                UpdateTransactionVM(
                    transactionApi = app.transactionApi,
                    authSessionProvider = { app.authPrefs.currentSession.value?.token }
                )
            }
        }
    }

    private val _uiState = MutableStateFlow(UpdateTransactionUiState())
    val uiState: StateFlow<UpdateTransactionUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<UpdateTransactionEffect>()
    val effect: SharedFlow<UpdateTransactionEffect> = _effect.asSharedFlow()

    fun load(transactionId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            runCatching {
                val authorization = authorizationHeader()
                    ?: throw IllegalStateException("Sesi login tidak ditemukan")

                val response = transactionApi.getTransaction(authorization, transactionId)
                if (!response.isSuccessful) {
                    val j = Json { ignoreUnknownKeys = true }
                    throw IllegalStateException(response.errorMessage(j))
                }

                response.body() ?: throw IllegalStateException("Respons server kosong")
            }.onSuccess { response ->
                    _uiState.update {
                        it.copy(
                            walletId = response.data.walletId.toString(),
                            selectedWalletName = "Wallet ID: ${response.data.walletId}",
                            amount = response.data.amount.toPlainString(),
                            categoryId = response.data.categoryId.toString(),
                            selectedCategoryName = response.category?.name ?: if (response.data.categoryId > 0) "Kategori #${response.data.categoryId}" else "",
                            description = response.data.description,
                            date = response.data.date,
                            isLoading = false,
                        )
                    }
            }.onFailure {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onWalletIdChanged(value: String) {
        _uiState.update { it.copy(walletId = value) }
    }

    fun onAmountChanged(value: String) {
        _uiState.update { it.copy(amount = value) }
    }

    fun onDescriptionChanged(value: String) {
        _uiState.update { it.copy(description = value) }
    }

    fun onDateChanged(value: String) {
        _uiState.update { it.copy(date = value) }
    }

    fun updateTransaction(id: Int) {
        val current = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            runCatching {
                val authorization = authorizationHeader()
                    ?: throw IllegalStateException("Sesi login tidak ditemukan")

                val response = transactionApi.updateTransaction(
                    authorization = authorization,
                    id = id,
                    body = UpdateTransactionReq(
                        walletId = current.walletId.toIntOrNull(),
                        amount = current.amount.toBigDecimalOrNull(),
                        categoryId = current.categoryId.toIntOrNull(),
                        description = current.description.ifBlank { null },
                        date = current.date.ifBlank { null },
                    ),
                )

                if (!response.isSuccessful) {
                    val j = Json { ignoreUnknownKeys = true }
                    throw IllegalStateException(response.errorMessage(j))
                }

                response.body() ?: throw IllegalStateException("Respons server kosong")
            }.onSuccess {
                _effect.emit(UpdateTransactionEffect.ShowMessage("Transaksi berhasil diperbarui"))
                _effect.emit(UpdateTransactionEffect.NavigateBack)
            }.onFailure { throwable ->
                _effect.emit(
                    UpdateTransactionEffect.ShowMessage(
                        throwable.message ?: "Gagal memperbarui transaksi"
                    )
                )
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    init {
        viewModelScope.launch {
            AppEventBus.event
                .filterIsInstance<AppEvent.WalletSelected>()
                .collect { event ->
                    _uiState.update {
                        it.copy(
                            walletId = event.wallet.id?.toString().orEmpty(),
                            selectedWalletName = event.wallet.name.orEmpty(),
                        )
                    }
                }
        }

        viewModelScope.launch {
            AppEventBus.event
                .filterIsInstance<AppEvent.CategorySelected>()
                .collect { event ->
                    _uiState.update {
                        it.copy(
                            categoryId = event.category.id.toString(),
                            selectedCategoryName = event.category.name,
                        )
                    }
                }
        }
    }

    private fun authorizationHeader(): String? {
        return authSessionProvider()?.let { "Bearer $it" }
    }
}

sealed interface UpdateTransactionEffect {
    data class ShowMessage(val message: String) : UpdateTransactionEffect
    data object NavigateBack : UpdateTransactionEffect
}
