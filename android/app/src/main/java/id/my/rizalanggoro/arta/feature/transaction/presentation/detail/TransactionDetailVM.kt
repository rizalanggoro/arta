package id.my.rizalanggoro.arta.feature.transaction.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewModelScope
import id.my.rizalanggoro.arta.core.application.MyApplication
import id.my.rizalanggoro.arta.feature.transaction.data.TransactionRepository
import id.my.rizalanggoro.arta.domain.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TransactionDetailVM(
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
                TransactionDetailVM(transactionRepository = repo)
            }
        }
    }

    private val _uiState = MutableStateFlow<Transaction?>(null)
    val uiState: StateFlow<Transaction?> = _uiState.asStateFlow()

    fun load(transactionId: Int) {
        viewModelScope.launch {
            transactionRepository.getTransactionById(transactionId)
                .onSuccess { tx -> _uiState.update { tx } }
                .onFailure { /* ignore; UI can show null */ }
        }
    }
}
