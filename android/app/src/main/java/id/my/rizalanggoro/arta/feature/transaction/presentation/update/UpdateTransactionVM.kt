package id.my.rizalanggoro.arta.feature.transaction.presentation.update

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewModelScope
import id.my.rizalanggoro.arta.core.application.MyApplication
import id.my.rizalanggoro.arta.feature.transaction.data.TransactionRepository
import id.my.rizalanggoro.arta.feature.category.data.CategoryRepository
import id.my.rizalanggoro.arta.feature.transaction.presentation.create.CreateTransactionUiState
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
    private val categoryRepository: CategoryRepository,
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
                val categoryRepo = app.categoryRepository
                UpdateTransactionVM(transactionRepository = repo, categoryRepository = categoryRepo)
            }
        }
    }

    private val _uiState = MutableStateFlow(CreateTransactionUiState())
    val uiState: StateFlow<CreateTransactionUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<UpdateTransactionEffect>()
    val effect: SharedFlow<UpdateTransactionEffect> = _effect.asSharedFlow()

    fun load(transactionId: Int) {
        viewModelScope.launch {
            // load categories for picker
            _uiState.update { it.copy(categoriesLoading = true) }
            categoryRepository.getCategories()
                .onSuccess { list -> _uiState.update { it.copy(categories = list, categoriesLoading = false) } }
                .onFailure { _uiState.update { it.copy(categoriesLoading = false) } }

            transactionRepository.getTransactionById(transactionId)
                .onSuccess { tx ->
                    _uiState.update {
                        it.copy(
                            walletId = tx.walletId.toString(),
                            type = tx.type,
                            amount = tx.amount.toString(),
                            categoryId = tx.categoryId.toString(),
                            description = tx.description,
                            date = tx.date,
                        )
                    }
                }
                .onFailure { /* ignore */ }
        }
    }

    fun onWalletIdChanged(value: String) { _uiState.update { it.copy(walletId = value) } }
    fun onTypeChanged(value: String) { _uiState.update { it.copy(type = value) } }
    fun onAmountChanged(value: String) { _uiState.update { it.copy(amount = value) } }
    fun onCategoryIdChanged(value: String) { _uiState.update { it.copy(categoryId = value) } }
    fun onDescriptionChanged(value: String) { _uiState.update { it.copy(description = value) } }
    fun onDateChanged(value: String) { _uiState.update { it.copy(date = value) } }

    fun updateTransaction(id: Int) {
        val current = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            transactionRepository.updateTransaction(
                id = id,
                walletId = current.walletId.toIntOrNull(),
                type = current.type.ifBlank { null },
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
