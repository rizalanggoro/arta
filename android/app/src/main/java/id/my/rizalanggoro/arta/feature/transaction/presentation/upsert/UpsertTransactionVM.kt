package id.my.rizalanggoro.arta.feature.transaction.presentation.upsert

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import id.my.rizalanggoro.arta.R
import id.my.rizalanggoro.arta.core.data.AuthPrefs
import id.my.rizalanggoro.arta.core.data.SelectedWalletPrefs
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.core.extension.errorMessage
import id.my.rizalanggoro.arta.core.extension.toApiFormat
import id.my.rizalanggoro.arta.openapi.apis.TransactionApi
import id.my.rizalanggoro.arta.openapi.models.CreateTransactionReq
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
import javax.inject.Inject

@HiltViewModel
class UpsertTransactionVM @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val transactionApi: TransactionApi,
    private val selectedWalletPrefs: SelectedWalletPrefs,
    private val authPrefs: AuthPrefs,
) : ViewModel() {
//    private var transactionId: Int = 0

    private val _uiState = MutableStateFlow(UpsertTransactionUiState())
    val uiState: StateFlow<UpsertTransactionUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<UpsertTransactionEffect>()
    val effect: SharedFlow<UpsertTransactionEffect> = _effect.asSharedFlow()

//    fun setTransactionId(value: Int) {
//        if (transactionId == value) return
//        transactionId = value
//        if (value == 0) {
//            prepareCreateMode()
//        } else {
//            loadTransaction(value)
//        }
//    }

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

    fun submit() {
        val current = _uiState.value
        val walletId = current.walletId.toIntOrNull()
        val categoryId = current.categoryId.toIntOrNull()
        val amount = current.amount.toDoubleOrNull()
        val resolvedWalletId = walletId ?: 0
        val resolvedCategoryId = categoryId ?: 0
        var hasError = false

        if (walletId == null || walletId <= 0) {
            _uiState.update { it.copy(walletIdError = "Wallet wajib dipilih") }
            hasError = true
        }

        if (categoryId == null || categoryId <= 0) {
            _uiState.update { it.copy(categoryError = "Kategori wajib dipilih") }
            hasError = true
        }

        if (amount == null || amount <= 0) {
            _uiState.update { it.copy(amountError = "Nominal transaksi tidak valid") }
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            runCatching {
                val authorization = authorizationHeader()
                    ?: throw IllegalStateException("Sesi login tidak ditemukan")

                if (current.isUpdate) {
                    val response = transactionApi.updateTransaction(
                        authorization = authorization,
                        id = current.transactionId,
                        body = UpdateTransactionReq(
                            walletId = resolvedWalletId,
                            amount = BigDecimal.valueOf(amount!!),
                            categoryId = resolvedCategoryId,
                            description = current.description.ifBlank { null },
                            date = current.date.toApiFormat(),
                        ),
                    )

                    if (!response.isSuccessful) {
                        throw IllegalStateException(response.errorMessage())
                    }

                    response.body() ?: throw IllegalStateException("Respons server kosong")
                } else {
                    val response = transactionApi.createTransaction(
                        authorization = authorization,
                        body = CreateTransactionReq(
                            amount = amount!!,
                            categoryId = resolvedCategoryId,
                            date = current.date.toApiFormat(),
                            walletId = resolvedWalletId,
                            description = current.description.ifBlank { null },
                        ),
                    )

                    if (!response.isSuccessful) {
                        throw IllegalStateException(response.errorMessage())
                    }

                    response.body() ?: throw IllegalStateException("Respons server kosong")
                }
            }.onSuccess {
                AppEventBus.emit(AppEvent.TransactionChanged)
                _effect.emit(
                    UpsertTransactionEffect.ShowMessage(
                        if (current.isUpdate) "Transaksi berhasil diperbarui" else "Transaksi berhasil dibuat",
                    )
                )
                _effect.emit(UpsertTransactionEffect.NavigateBack)
            }.onFailure { throwable ->
                _effect.emit(
                    UpsertTransactionEffect.ShowMessage(
                        throwable.message ?: context.getString(R.string.client_error)
                    )
                )
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

//    private fun loadTransaction(id: Int) {
//        viewModelScope.launch {
//            _uiState.update { it.copy(transactionId = id, isUpdate = true, isLoading = true) }
//            runCatching {
//                val authorization = authorizationHeader()
//                    ?: throw IllegalStateException("Sesi login tidak ditemukan")
//
//                val response = transactionApi.getTransaction(authorization, id)
//                if (!response.isSuccessful) {
//                    throw IllegalStateException(response.errorMessage())
//                }
//
//                response.body() ?: throw IllegalStateException("Respons server kosong")
//            }.onSuccess { response ->
//                _uiState.update {
//                    it.copy(
//                        transactionId = id,
//                        isUpdate = true,
//                        walletId = response.data.walletId.toString(),
//                        selectedWalletName = "Wallet ID: ${response.data.walletId}",
//                        amount = response.data.amount.toString(),
//                        categoryId = response.data.categoryId.toString(),
//                        selectedCategoryName = response.category?.name
//                            ?: if (response.data.categoryId > 0) "Kategori #${response.data.categoryId}" else "",
//                        description = response.data.description,
//                        date = response.data.date,
//                        isLoading = false,
//                        walletIdError = null,
//                        amountError = null,
//                        categoryError = null,
//                        dateError = null,
//                    )
//                }
//            }.onFailure { throwable ->
//                _uiState.update {
//                    it.copy(
//                        isLoading = false,
//                        dateError = throwable.message,
//                    )
//                }
//            }
//        }
//    }

    private fun authorizationHeader(): String? {
        return authPrefs.currentSession.value?.token?.let { "Bearer $it" }
    }

    init {
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

sealed interface UpsertTransactionEffect {
    data class ShowMessage(val message: String) : UpsertTransactionEffect
    data object NavigateBack : UpsertTransactionEffect
}