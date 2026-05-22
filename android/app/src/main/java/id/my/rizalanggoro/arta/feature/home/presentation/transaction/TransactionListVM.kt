package id.my.rizalanggoro.arta.feature.home.presentation.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import id.my.rizalanggoro.arta.core.application.MyApplication
import id.my.rizalanggoro.arta.core.network.RetrofitProvider
import id.my.rizalanggoro.arta.feature.transaction.data.TransactionRepository
import id.my.rizalanggoro.arta.feature.wallet.walletApiErrorMessage
import id.my.rizalanggoro.arta.openapi.apis.WalletApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TransactionListVM(
    private val transactionRepository: TransactionRepository,
    private val walletApi: WalletApi,
) : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as MyApplication
                val txRepo = TransactionRepository(
                    apiService = RetrofitProvider.create(id.my.rizalanggoro.arta.feature.transaction.data.TransactionApiService::class.java),
                    authSessionProvider = { app.authPrefs.currentSession.value },
                )
                val walletApi = app.walletApi
                TransactionListVM(transactionRepository = txRepo, walletApi = walletApi)
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
            runCatching {
                val response = walletApi.listWallets()
                if (!response.isSuccessful) {
                    throw IllegalStateException(response.walletApiErrorMessage())
                }

                response.body()?.wallets.orEmpty().mapNotNull { it.data }
            }
                .onSuccess { wallets ->
                    val allTxs = mutableListOf<id.my.rizalanggoro.arta.domain.Transaction>()
                    for (w in wallets) {
                        val walletId = w.id ?: continue
                        transactionRepository.listTransactionsByWallet(walletId)
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
