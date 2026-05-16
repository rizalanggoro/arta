package id.my.rizalanggoro.arta.feature.transaction.presentation.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewModelScope
import id.my.rizalanggoro.arta.core.application.MyApplication
import id.my.rizalanggoro.arta.feature.transaction.data.TransactionRepository
import id.my.rizalanggoro.arta.domain.Category
import id.my.rizalanggoro.arta.domain.Wallet
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import id.my.rizalanggoro.arta.feature.category.presentation.select.CategorySelectionBus
import id.my.rizalanggoro.arta.feature.wallet.presentation.select.WalletSelectionBus

class CreateTransactionVM(
    private val transactionRepository: TransactionRepository,
) : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as MyApplication)
                val repo = id.my.rizalanggoro.arta.feature.transaction.data.TransactionRepository(
                    apiService = id.my.rizalanggoro.arta.core.network.RetrofitProvider.create(
                        id.my.rizalanggoro.arta.feature.transaction.data.TransactionApiService::class.java
                    ),
                    authSessionProvider = { app.authPrefs.currentSession.value },
                )
                CreateTransactionVM(transactionRepository = repo)
            }
        }
    }

    init {
        viewModelScope.launch {
            CategorySelectionBus.selectedCategory.collect { category ->
                onSelectCategory(category)
            }
        }
    }

    init {
        viewModelScope.launch {
            WalletSelectionBus.selectedWallet.collect { wallet ->
                onSelectWallet(wallet)
            }
        }
    }

    private val _uiState = MutableStateFlow(CreateTransactionUiState())
    val uiState: StateFlow<CreateTransactionUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<CreateTransactionEffect>()
    val effect: SharedFlow<CreateTransactionEffect> = _effect.asSharedFlow()

    fun onWalletIdChanged(value: String) {
        _uiState.update { it.copy(walletId = value, selectedWalletName = "", walletIdError = null) }
    }

    fun onTypeChanged(value: String) {
        _uiState.update { it.copy(type = value, typeError = null) }
    }

    fun onAmountChanged(value: String) {
        _uiState.update { it.copy(amount = value, amountError = null) }
    }

    fun onCategoryIdChanged(value: String) {
        _uiState.update { it.copy(categoryId = value, selectedCategoryName = "") }
    }

    fun onSelectCategory(category: Category) {
        _uiState.update {
            it.copy(
                categoryId = category.id.toString(),
                selectedCategoryName = category.name,
            )
        }
    }

    fun onSelectWallet(wallet: Wallet) {
        _uiState.update {
            it.copy(
                walletId = wallet.id.toString(),
                selectedWalletName = wallet.name,
                walletIdError = null,
            )
        }
    }

    fun onDescriptionChanged(value: String) {
        _uiState.update { it.copy(description = value) }
    }

    fun onDateChanged(value: String) {
        _uiState.update { it.copy(date = value, dateError = null) }
    }

    fun createTransaction() {
        val current = _uiState.value
        var hasError = false

        val walletId = current.walletId.toIntOrNull()
        if (walletId == null || walletId <= 0) {
            _uiState.update { it.copy(walletIdError = "Wallet ID wajib diisi") }
            hasError = true
        }

        if (current.type.isBlank()) {
            _uiState.update { it.copy(typeError = "Tipe transaksi wajib diisi") }
            hasError = true
        }

        val amount = current.amount.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            _uiState.update { it.copy(amountError = "Jumlah harus berupa angka lebih dari 0") }
            hasError = true
        }

        if (current.date.isBlank()) {
            _uiState.update { it.copy(dateError = "Tanggal wajib diisi") }
            hasError = true
        }

        if (hasError) {
            viewModelScope.launch { _effect.emit(CreateTransactionEffect.ShowMessage("Periksa kembali isian transaksi")) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            transactionRepository.createTransaction(
                walletId = walletId!!,
                type = current.type,
                amount = amount!!,
                categoryId = current.categoryId.toIntOrNull(),
                description = current.description,
                date = current.date,
            ).onSuccess { tx ->
                _effect.emit(CreateTransactionEffect.ShowMessage("Transaksi berhasil dibuat"))
                _effect.emit(CreateTransactionEffect.NavigateBack)
            }.onFailure { throwable ->
                _effect.emit(CreateTransactionEffect.ShowMessage(throwable.message ?: "Gagal membuat transaksi"))
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }
}

sealed interface CreateTransactionEffect {
    data class ShowMessage(val message: String) : CreateTransactionEffect
    data object NavigateBack : CreateTransactionEffect
}
