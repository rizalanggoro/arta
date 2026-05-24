package id.my.rizalanggoro.arta.feature.transaction.presentation.upsert

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.rizalanggoro.arta.core.data.AuthPrefs
import id.my.rizalanggoro.arta.core.data.SelectedWalletPrefs
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.core.extension.errorMessage
import id.my.rizalanggoro.arta.openapi.apis.TransactionApi
import id.my.rizalanggoro.arta.openapi.models.CreateTransactionReq
import id.my.rizalanggoro.arta.openapi.models.DomainCategory
import id.my.rizalanggoro.arta.openapi.models.DomainWallet
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
    private val transactionApi: TransactionApi,
    private val selectedWalletPrefs: SelectedWalletPrefs,
    private val authPrefs: AuthPrefs,
) : ViewModel() {
    private var transactionId: Int = 0

    private val _uiState = MutableStateFlow(UpsertTransactionUiState())
    val uiState: StateFlow<UpsertTransactionUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<UpsertTransactionEffect>()
    val effect: SharedFlow<UpsertTransactionEffect> = _effect.asSharedFlow()

    fun setTransactionId(value: Int) {
        if (transactionId == value) return
        transactionId = value
        if (value == 0) {
            prepareCreateMode()
        } else {
            loadTransaction(value)
        }
    }

    fun onWalletIdChanged(value: String) {
        _uiState.update {
            it.copy(
                walletId = value,
                walletIdError = null,
            )
        }
    }

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

    fun onCategoryIdChanged(value: String) {
        _uiState.update {
            it.copy(
                categoryId = value,
                categoryError = null,
            )
        }
    }

    fun onDateChanged(value: String) {
        _uiState.update {
            it.copy(
                date = value,
                dateError = null,
            )
        }
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

        if (current.date.isBlank() || runCatching { java.time.OffsetDateTime.parse(current.date) }.isFailure) {
            _uiState.update { it.copy(dateError = "Tanggal harus format ISO 8601") }
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
                            date = current.date,
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
                            amount = BigDecimal.valueOf(amount!!),
                            categoryId = resolvedCategoryId,
                            date = current.date,
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
                        throwable.message ?: "Terjadi kesalahan tak terduga"
                    )
                )
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun prepareCreateMode() {
        val wallet = selectedWalletPrefs.selectedWallet.value
        _uiState.update {
            it.copy(
                transactionId = 0,
                isUpdate = false,
                walletId = wallet?.id?.toString().orEmpty(),
                selectedWalletName = wallet?.name.orEmpty(),
                amount = "",
                categoryId = "",
                selectedCategoryName = "",
                description = "",
                date = currentIsoDate(),
                walletIdError = null,
                amountError = null,
                categoryError = null,
                dateError = null,
                isLoading = false,
            )
        }
    }

    private fun loadTransaction(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(transactionId = id, isUpdate = true, isLoading = true) }
            runCatching {
                val authorization = authorizationHeader()
                    ?: throw IllegalStateException("Sesi login tidak ditemukan")

                val response = transactionApi.getTransaction(authorization, id)
                if (!response.isSuccessful) {
                    throw IllegalStateException(response.errorMessage())
                }

                response.body() ?: throw IllegalStateException("Respons server kosong")
            }.onSuccess { response ->
                _uiState.update {
                    it.copy(
                        transactionId = id,
                        isUpdate = true,
                        walletId = response.data.walletId.toString(),
                        selectedWalletName = "Wallet ID: ${response.data.walletId}",
                        amount = response.data.amount.toPlainString(),
                        categoryId = response.data.categoryId.toString(),
                        selectedCategoryName = response.category?.name
                            ?: if (response.data.categoryId > 0) "Kategori #${response.data.categoryId}" else "",
                        description = response.data.description,
                        date = response.data.date,
                        isLoading = false,
                        walletIdError = null,
                        amountError = null,
                        categoryError = null,
                        dateError = null,
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        dateError = throwable.message,
                    )
                }
            }
        }
    }

    private fun authorizationHeader(): String? {
        return authPrefs.currentSession.value?.token?.let { "Bearer $it" }
    }

    init {
        prepareCreateMode()

        viewModelScope.launch {
            AppEventBus.event
                .filterIsInstance<AppEvent.WalletSelected>()
                .collect { event ->
                    updateWallet(event.wallet)
                }
        }

        viewModelScope.launch {
            AppEventBus.event
                .filterIsInstance<AppEvent.CategorySelected>()
                .collect { event ->
                    updateCategory(event.category)
                }
        }
    }

    private fun updateWallet(wallet: DomainWallet) {
        _uiState.update {
            it.copy(
                walletId = wallet.id?.toString().orEmpty(),
                selectedWalletName = wallet.name.orEmpty(),
                walletIdError = null,
            )
        }
    }

    private fun updateCategory(category: DomainCategory) {
        _uiState.update {
            it.copy(
                categoryId = category.id?.toString().orEmpty(),
                selectedCategoryName = category.name,
                categoryError = null,
            )
        }
    }
}

sealed interface UpsertTransactionEffect {
    data class ShowMessage(val message: String) : UpsertTransactionEffect
    data object NavigateBack : UpsertTransactionEffect
}