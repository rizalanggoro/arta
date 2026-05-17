package id.my.rizalanggoro.arta.feature.home.presentation.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import id.my.rizalanggoro.arta.core.application.MyApplication
import id.my.rizalanggoro.arta.core.network.RetrofitProvider
import id.my.rizalanggoro.arta.feature.transaction.data.TransactionRepository
import id.my.rizalanggoro.arta.feature.wallet.data.WalletRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TransactionListVM(
    private val transactionRepository: TransactionRepository,
    private val walletRepository: WalletRepository,
) : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as MyApplication
                val txRepo = TransactionRepository(
                    apiService = RetrofitProvider.create(id.my.rizalanggoro.arta.feature.transaction.data.TransactionApiService::class.java),
                    authSessionProvider = { app.authPrefs.currentSession.value },
                )
                val walletRepo = app.walletRepository
                TransactionListVM(transactionRepository = txRepo, walletRepository = walletRepo)
            }
        }
    }

    private val _uiState = MutableStateFlow(TransactionListUiState())
    val uiState: StateFlow<TransactionListUiState> = _uiState.asStateFlow()

    init {
        loadTransactions()
    }

    fun loadTransactions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            walletRepository.getWallets()
                .onSuccess { wallets ->
                    val allTxs = mutableListOf<id.my.rizalanggoro.arta.domain.Transaction>()
                    for (w in wallets) {
                        transactionRepository.listTransactionsByWallet(w.id)
                            .onSuccess { txs -> allTxs.addAll(txs) }
                            .onFailure { /* ignore per-wallet failure for now */ }
                    }
                    _uiState.update { it.copy(transactions = allTxs.sortedByDescending { it.date }, isLoading = false) }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = throwable.message ?: "Gagal memuat transaksi") }
                }
        }
    }
}
