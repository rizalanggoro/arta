package id.my.rizalanggoro.arta.feature.transaction.presentation.create

import id.my.rizalanggoro.arta.domain.Category

data class CreateTransactionUiState(
    val walletId: String = "",
    val type: String = "",
    val amount: String = "",
    val categoryId: String = "",
    val description: String = "",
    val date: String = "",
    val categories: List<Category> = emptyList(),
    val categoriesLoading: Boolean = false,
    val walletIdError: String? = null,
    val typeError: String? = null,
    val amountError: String? = null,
    val dateError: String? = null,
    val isLoading: Boolean = false,
)
