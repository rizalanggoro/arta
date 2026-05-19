package id.my.rizalanggoro.arta.feature.transaction.presentation.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import id.my.rizalanggoro.arta.core.application.MyApplication
import id.my.rizalanggoro.arta.core.data.SelectedWalletPrefs
import id.my.rizalanggoro.arta.core.extension.toApiFormat
import id.my.rizalanggoro.arta.feature.category.presentation.select.CategorySelectionBus
import id.my.rizalanggoro.arta.feature.transaction.data.TransactionRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateTransactionVM(
    private val transactionRepository: TransactionRepository,
    private val selectedWalletPrefs: SelectedWalletPrefs,
) : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as MyApplication)
                CreateTransactionVM(
                    transactionRepository = app.transactionRepository,
                    selectedWalletPrefs = app.selectedWalletPrefs
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

        val walletId = current.wallet?.id
        if (walletId == null || walletId <= 0) {
            _uiState.update { it.copy(walletError = "Wallet aktif wajib dipilih") }
            hasError = true
        }

        val amount = current.amount.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            _uiState.update { it.copy(amountError = "Jumlah harus berupa angka lebih dari 0") }
            hasError = true
        }

        if (current.category == null) {
            _uiState.update { it.copy(categoryError = "Kategori wajib dipilih") }
            hasError = true
        }

        if (hasError) {
            viewModelScope.launch { _effect.emit(CreateTransactionEvent.ShowMessage("Periksa kembali isian transaksi")) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            transactionRepository.createTransaction(
                walletId = walletId!!,
                amount = amount!!,
                categoryId = current.category!!.id,
                description = current.description,
                date = current.date.toApiFormat(),
            ).onSuccess { tx ->
                _effect.emit(CreateTransactionEvent.ShowMessage("Transaksi berhasil dibuat"))
                _effect.emit(CreateTransactionEvent.Success)
            }.onFailure { throwable ->
                _effect.emit(
                    CreateTransactionEvent.ShowMessage(
                        throwable.message ?: "Gagal membuat transaksi"
                    )
                )
            }
            _uiState.update { it.copy(isLoading = false) }
        }
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
            CategorySelectionBus.selectedCategory.collect { category ->
                _uiState.update {
                    it.copy(
                        category = category,
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
