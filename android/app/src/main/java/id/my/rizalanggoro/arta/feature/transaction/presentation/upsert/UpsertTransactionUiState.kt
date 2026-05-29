package id.my.rizalanggoro.arta.feature.transaction.presentation.upsert

import id.my.rizalanggoro.arta.openapi.models.DomainCategory
import id.my.rizalanggoro.arta.openapi.models.DomainWallet

data class UpsertTransactionUiState(
    val selectedWallet: DomainWallet? = null,
    val selectedCategory: DomainCategory? = null,
    val date: Long = System.currentTimeMillis(),
    val isDatePickerOpen: Boolean = false,
    val amount: String = "",
    val amountError: String? = null,
    val description: String = "",
    val categoryError: String? = null,
    val isUpdate: Boolean = false,
    val isLoading: Boolean = false,

    val transactionId: Int = 0,
) {
    sealed class Event {
        data class ShowMessage(val message: String) : Event()
    }
}