package id.my.rizalanggoro.arta.feature.home.presentation.transaction

import id.my.rizalanggoro.arta.openapi.models.DomainWallet
import id.my.rizalanggoro.arta.openapi.models.DtoTransaction

data class TransactionListUiState(
    val selectedWallet: DomainWallet? = null,
    val transactions: List<DtoTransaction> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,

    val targetDeleteTransactionId: Int? = null,
    val isDeleting: Boolean = false,
)
