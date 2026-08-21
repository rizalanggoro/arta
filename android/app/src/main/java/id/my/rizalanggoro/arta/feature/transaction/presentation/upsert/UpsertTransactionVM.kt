package id.my.rizalanggoro.arta.feature.transaction.presentation.upsert

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import id.my.rizalanggoro.arta.R
import id.my.rizalanggoro.arta.core.application.route.TransactionRoute
import id.my.rizalanggoro.arta.core.data.AuthPrefs
import id.my.rizalanggoro.arta.core.data.SelectedWalletPrefs
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.core.extension.authorization
import id.my.rizalanggoro.arta.core.extension.errorMessage
import id.my.rizalanggoro.arta.core.extension.toApiFormat
import id.my.rizalanggoro.arta.core.extension.toMillis
import id.my.rizalanggoro.arta.openapi.apis.TransactionApi
import id.my.rizalanggoro.arta.openapi.models.CreateTransactionReq
import id.my.rizalanggoro.arta.openapi.models.UpdateTransactionReq
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

@HiltViewModel(assistedFactory = UpsertTransactionVM.Factory::class)
class UpsertTransactionVM @AssistedInject constructor(
    @param:ApplicationContext private val context: Context,
    @Assisted private val navKey: TransactionRoute.Upsert,
    private val transactionApi: TransactionApi,
    private val selectedWalletPrefs: SelectedWalletPrefs,
    private val authPrefs: AuthPrefs,
) : ViewModel() {
    @AssistedFactory
    interface Factory {
        fun create(navKey: TransactionRoute.Upsert): UpsertTransactionVM
    }

    private val _uiState = MutableStateFlow(UpsertTransactionUiState())
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<UpsertTransactionUiState.Event>()
    val event = _event.asSharedFlow()

    // Reused across retries so the server can dedupe; rotated only when the
    // server definitively rejected a submission (see onFailure in onSubmitClicked).
    private var idempotencyKey: String = UUID.randomUUID().toString()

    fun onAmountChanged(value: String) {
        _uiState.update {
            it.copy(
                amount = value,
                amountError = null,
            )
        }
    }

    fun onDescriptionChanged(value: String) {
        _uiState.update { it.copy(description = value) }
    }

    fun onSelectDateClicked() = _uiState.update {
        it.copy(isDatePickerOpen = true)
    }

    fun onDatePickerDismissed() = _uiState.update {
        it.copy(isDatePickerOpen = false)
    }

    fun onDateSelected(value: Long?) = _uiState.update {
        if (value == null) return@update it
        it.copy(
            date = value,
            isDatePickerOpen = false
        )
    }

    fun onSubmitClicked() {
        val current = _uiState.value
        val walletId = current.selectedWallet?.id ?: return
        val categoryId = current.selectedCategory?.id
        val amount = current.amount.toDoubleOrNull()
        var hasError = false

        if (categoryId == null || categoryId <= 0) {
            _uiState.update { it.copy(categoryError = "Kategori wajib dipilih") }
            hasError = true
        }

        if (amount == null || amount <= 0) {
            _uiState.update { it.copy(amountError = "Nominal transaksi tidak valid") }
            hasError = true
        }

        if (hasError) return

        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            runCatching {
                if (current.isUpdate) {
                    val response = transactionApi.updateTransaction(
                        authorization = authPrefs.authorization(),
                        id = current.transactionId,
                        body = UpdateTransactionReq(
                            walletId = walletId,
                            amount = amount!!,
                            categoryId = categoryId!!,
                            description = current.description,
                            date = current.date.toApiFormat(),
                        ),
                    )

                    if (!response.isSuccessful) {
                        throw IllegalStateException(response.errorMessage())
                    }

                    response.body() ?: throw IllegalStateException("Respons server kosong")
                } else {
                    val response = transactionApi.createTransaction(
                        authorization = authPrefs.authorization(),
                        body = CreateTransactionReq(
                            amount = amount!!,
                            categoryId = categoryId!!,
                            date = current.date.toApiFormat(),
                            walletId = walletId,
                            description = current.description,
                        ),
                        idempotencyKey = idempotencyKey,
                    )

                    if (!response.isSuccessful) {
                        throw IllegalStateException(response.errorMessage())
                    }

                    response.body() ?: throw IllegalStateException(
                        context.getString(R.string.server_empty_error)
                    )
                }
            }.onSuccess {
                AppEventBus.emit(AppEvent.TransactionChanged)
            }.onFailure { throwable ->
                if (throwable is IllegalStateException) {
                    idempotencyKey = UUID.randomUUID().toString()
                }
                _event.emit(
                    UpsertTransactionUiState.Event.ShowMessage(
                        throwable.message ?: context.getString(R.string.client_error)
                    )
                )
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun loadTransaction() = viewModelScope.launch {
        _uiState.update {
            it.copy(
                transactionId = navKey.transactionId,
                isUpdate = true,
                isLoading = true
            )
        }
        runCatching {
            val response = transactionApi.getTransaction(
                authorization = authPrefs.authorization(),
                id = navKey.transactionId
            )

            if (!response.isSuccessful) throw IllegalStateException(response.errorMessage())

            response.body() ?: throw IllegalStateException(
                context.getString(R.string.server_empty_error)
            )
        }.onSuccess { body ->
            val transaction = body.data
            val category = body.category
            _uiState.update {
                it.copy(
                    isLoading = false,
                    amount = transaction.amount.toString(),
                    selectedCategory = category,
                    date = transaction.date.toMillis(),
                    description = transaction.description,
                )
            }
        }.onFailure { throwable ->
            _uiState.update {
                it.copy(
                    isLoading = false,
                )
            }
        }
    }

    init {
        if (navKey.transactionId > 0)
            loadTransaction()

        viewModelScope.launch {
            selectedWalletPrefs.selectedWallet.collect { wallet ->
                _uiState.update {
                    it.copy(
                        selectedWallet = wallet
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
                            selectedCategory = event.category
                        )
                    }
                }
        }
    }
}