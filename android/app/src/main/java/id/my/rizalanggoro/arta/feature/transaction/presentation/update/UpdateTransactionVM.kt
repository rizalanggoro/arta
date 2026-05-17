package id.my.rizalanggoro.arta.feature.transaction.presentation.update

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewModelScope
import id.my.rizalanggoro.arta.core.application.MyApplication
import id.my.rizalanggoro.arta.feature.transaction.data.TransactionRepository
import id.my.rizalanggoro.arta.feature.category.presentation.select.CategorySelectionBus
import id.my.rizalanggoro.arta.feature.transaction.presentation.create.CreateTransactionUiState
import id.my.rizalanggoro.arta.domain.Category
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UpdateTransactionVM(
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
                UpdateTransactionVM(transactionRepository = repo)
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

    private val _uiState = MutableStateFlow(CreateTransactionUiState())
    val uiState: StateFlow<CreateTransactionUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<UpdateTransactionEffect>()
    val effect: SharedFlow<UpdateTransactionEffect> = _effect.asSharedFlow()

    fun load(transactionId: Int) {
        viewModelScope.launch {
            transactionRepository.getTransactionById(transactionId)
                .onSuccess { tx ->
                    _uiState.update {
                        it.copy(
                            walletId = tx.walletId.toString(),
                            amount = tx.amount.toString(),
                            categoryId = tx.categoryId.toString(),
                            selectedCategoryName = if (tx.categoryId > 0) "Kategori #${tx.categoryId}" else "",
                            description = tx.description,
                            date = tx.date,
                        )
                    }
                }
                .onFailure { /* ignore */ }
        }
    }

    fun onWalletIdChanged(value: String) { _uiState.update { it.copy(walletId = value) } }
    fun onAmountChanged(value: String) { _uiState.update { it.copy(amount = value) } }
    fun onCategoryIdChanged(value: String) { _uiState.update { it.copy(categoryId = value, selectedCategoryName = "") } }
    fun onDescriptionChanged(value: String) { _uiState.update { it.copy(description = value) } }
    fun onDateChanged(value: String) { _uiState.update { it.copy(date = value) } }

    fun onSelectCategory(category: Category) {
        _uiState.update {
            it.copy(
                categoryId = category.id.toString(),
                selectedCategoryName = category.name,
            )
        }
    }

    fun updateTransaction(id: Int) {
        val current = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            transactionRepository.updateTransaction(
                id = id,
                walletId = current.walletId.toIntOrNull(),
                amount = current.amount.toDoubleOrNull(),
                categoryId = current.categoryId.toIntOrNull(),
                description = current.description.ifBlank { null },
                date = current.date.ifBlank { null },
            ).onSuccess {
                _effect.emit(UpdateTransactionEffect.ShowMessage("Transaksi berhasil diperbarui"))
                _effect.emit(UpdateTransactionEffect.NavigateBack)
            }.onFailure { throwable ->
                _effect.emit(UpdateTransactionEffect.ShowMessage(throwable.message ?: "Gagal memperbarui transaksi"))
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }
}

sealed interface UpdateTransactionEffect {
    data class ShowMessage(val message: String) : UpdateTransactionEffect
    data object NavigateBack : UpdateTransactionEffect
}
