package id.my.rizalanggoro.arta.feature.home.presentation.transaction

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
import id.my.rizalanggoro.arta.core.extension.authorization
import id.my.rizalanggoro.arta.core.extension.errorMessage
import id.my.rizalanggoro.arta.openapi.apis.TransactionApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionListVM @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val transactionApi: TransactionApi,
    private val authPrefs: AuthPrefs,
    private val selectedWalletPrefs: SelectedWalletPrefs
) : ViewModel() {
    private val _uiState = MutableStateFlow(TransactionListUiState())
    val uiState = _uiState.asStateFlow()

    fun loadTransactions(isRefresh: Boolean = false) = viewModelScope.launch {
        val walletId = _uiState.value.selectedWallet?.id ?: return@launch

        _uiState.update {
            it.copy(
                isLoading = when {
                    isRefresh -> it.isLoading
                    else -> true
                },
                isRefreshing = when {
                    isRefresh -> true
                    else -> it.isRefreshing
                },
                errorMessage = null
            )
        }
        runCatching {
            val response = transactionApi.listTransactions(
                authorization = authPrefs.authorization(),
                walletId = walletId,
                includeCategory = true
            )
            if (!response.isSuccessful) {
                throw IllegalStateException(response.errorMessage())
            }

            response.body() ?: throw IllegalStateException(
                context.getString(
                    R.string.server_empty_error
                )
            )
        }.onSuccess { response ->
            _uiState.update {
                it.copy(
                    isLoading = when {
                        isRefresh -> it.isLoading
                        else -> false
                    },
                    isRefreshing = when {
                        isRefresh -> false
                        else -> it.isRefreshing
                    },
                    transactions = response.transactions
                )
            }
        }.onFailure { throwable ->
            _uiState.update {
                it.copy(
                    isLoading = when {
                        isRefresh -> it.isLoading
                        else -> false
                    },
                    isRefreshing = when {
                        isRefresh -> false
                        else -> it.isRefreshing
                    },
                    errorMessage = throwable.message ?: context.getString(
                        R.string.client_error
                    )
                )
            }
        }
    }

    fun onDeleteTransactionDialogDismissed() = _uiState.update {
        it.copy(targetDeleteTransactionId = null)
    }

    fun onDeleteTransactionDialogClicked() = viewModelScope.launch {
        val transactionId = _uiState.value.targetDeleteTransactionId ?: return@launch

        _uiState.update {
            it.copy(isDeleting = true)
        }

        runCatching {
            val authorization = authorizationHeader()
                ?: throw IllegalStateException("Sesi login tidak ditemukan")

            val response = transactionApi.deleteTransaction(
                authorization = authorization,
                id = transactionId
            )
            if (!response.isSuccessful) throw IllegalStateException(response.errorMessage())

            response.body() ?: throw IllegalStateException(
                context.getString(
                    R.string.server_empty_error
                )
            )
        }.onSuccess {
            AppEventBus.emit(AppEvent.TransactionChanged)
            _uiState.update {
                it.copy(
                    isDeleting = false,
                    targetDeleteTransactionId = null
                )
            }
        }.onFailure { throwable ->
            _uiState.update {
                it.copy(
                    isDeleting = false,
                    targetDeleteTransactionId = null,
                    errorMessage = throwable.message ?: context.getString(
                        R.string.client_error
                    )
                )
            }
        }
    }

    private fun authorizationHeader(): String? {
        return authPrefs.currentSession.value?.token?.let { "Bearer $it" }
    }

    init {
        viewModelScope.launch {
            selectedWalletPrefs.selectedWallet.collect { wallet ->
                _uiState.update {
                    it.copy(selectedWallet = wallet)
                }
                loadTransactions()
            }
        }

        viewModelScope.launch {
            AppEventBus.event.collect { event ->
                when (event) {
                    in listOf(
                        AppEvent.TransactionChanged,
                        AppEvent.CategoryChanged
                    ) -> loadTransactions(isRefresh = true)

                    is AppEvent.TransactionActionSheet.OnDeleteClicked -> _uiState.update {
                        it.copy(
                            targetDeleteTransactionId = event.transactionId,
                        )
                    }

                    else -> {}
                }
            }
        }
    }
}
