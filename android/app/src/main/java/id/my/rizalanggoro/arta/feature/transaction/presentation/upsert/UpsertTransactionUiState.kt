package id.my.rizalanggoro.arta.feature.transaction.presentation.upsert

import id.my.rizalanggoro.arta.openapi.models.DomainCategory
import id.my.rizalanggoro.arta.openapi.models.DomainWallet

data class UpsertTransactionUiState(
    val selectedWallet: DomainWallet? = null,
    val selectedCategory: DomainCategory? = null,
    val date: Long = System.currentTimeMillis(),

    val isDatePickerOpen: Boolean = false,

    val transactionId: Int = 0,
    val isUpdate: Boolean = false,
    val walletId: String = "",
    val selectedWalletName: String = "",
    val amount: String = "",
    val categoryId: String = "",
    val selectedCategoryName: String = "",
    val description: String = "",
    val walletIdError: String? = null,
    val amountError: String? = null,
    val categoryError: String? = null,
    val isLoading: Boolean = false,
) {
    sealed class Event {
        data class ShowMessage(val message: String) : Event()
    }
}