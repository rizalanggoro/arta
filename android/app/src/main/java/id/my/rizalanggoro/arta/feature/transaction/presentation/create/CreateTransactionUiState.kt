package id.my.rizalanggoro.arta.feature.transaction.presentation.create

import id.my.rizalanggoro.arta.openapi.models.DomainCategory
import id.my.rizalanggoro.arta.openapi.models.DomainWallet

data class CreateTransactionUiState(
    val wallet: DomainWallet? = null,
    val category: DomainCategory? = null,
    val amount: String = "",
    val description: String = "",
    val date: Long = System.currentTimeMillis(),
    val amountError: String? = null,
    val categoryError: String? = null,
    val dateError: String? = null,
    val isLoading: Boolean = false,
    val isDatePickerOpen: Boolean = false,
)
