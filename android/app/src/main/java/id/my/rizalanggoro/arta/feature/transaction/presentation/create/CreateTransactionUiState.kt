package id.my.rizalanggoro.arta.feature.transaction.presentation.create

import id.my.rizalanggoro.arta.domain.Category
import id.my.rizalanggoro.arta.domain.Wallet

data class CreateTransactionUiState(
    val wallet: Wallet? = null,
    val category: Category? = null,
    val amount: String = "",
    val description: String = "",
    val date: Long = System.currentTimeMillis(),
    val walletError: String? = null,
    val amountError: String? = null,
    val categoryError: String? = null,
    val dateError: String? = null,
    val isLoading: Boolean = false,
    val isDatePickerOpen: Boolean = false,
)
