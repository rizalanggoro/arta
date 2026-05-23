package id.my.rizalanggoro.arta.feature.home.presentation.transaction

// replaced walletApiErrorMessage usages with core.errorMessage()
// use OpenAPI models directly (`DtoTransaction.data`) instead of mapper extension
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import id.my.rizalanggoro.arta.core.application.MyApplication
import id.my.rizalanggoro.arta.core.extension.errorMessage
import id.my.rizalanggoro.arta.openapi.apis.TransactionApi
import id.my.rizalanggoro.arta.openapi.apis.WalletApi
import id.my.rizalanggoro.arta.openapi.models.DomainTransaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TransactionListVM(
    private val transactionApi: TransactionApi,
    private val walletApi: WalletApi,
    private val authSessionProvider: () -> String?,
) : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as MyApplication
                val walletApi = app.walletApi
                TransactionListVM(
                    transactionApi = app.transactionApi,
                    walletApi = walletApi,
                    authSessionProvider = { app.authPrefs.currentSession.value?.token },
                )
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
                val authorization = authorizationHeader()
                    ?: throw IllegalStateException("Sesi login tidak ditemukan")

                val response = walletApi.listWallets(authorization)
                if (!response.isSuccessful) {
                    throw IllegalStateException(response.errorMessage())
                }

                response.body()?.wallets.orEmpty().mapNotNull { it.data }
            }
                .onSuccess { wallets ->
                    val allTxs = mutableListOf<DomainTransaction>()
                    for (w in wallets) {
                        val walletId = w.id ?: continue
                        runCatching {
                            val authorization = authorizationHeader()
                                ?: throw IllegalStateException("Sesi login tidak ditemukan")

                            val txResponse =
                                transactionApi.listTransactions(authorization, walletId)
                            if (!txResponse.isSuccessful) {
                                throw IllegalStateException(txResponse.errorMessage())
                            }

                            txResponse.body()
                                ?: throw IllegalStateException("Respons server kosong")
                        }.onSuccess { txResponse ->
                            allTxs.addAll(txResponse.transactions.map { it.data })
                        }.onFailure { /* ignore per-wallet failure for now */ }
                    }
                    _uiState.update {
                        it.copy(
                            transactions = allTxs.sortedByDescending { it.date },
                            isLoading = false
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.message ?: "Gagal memuat transaksi"
                        )
                    }
                }
        }
    }

    private fun authorizationHeader(): String? {
        return authSessionProvider()?.let { "Bearer $it" }
    }
}
