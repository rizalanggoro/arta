package id.my.rizalanggoro.arta.feature.transaction.presentation.create

data class CreateTransactionUiState(
    val walletId: String = "",
    val selectedWalletName: String = "",
    val type: String = "",
    val amount: String = "",
    val categoryId: String = "",
    val selectedCategoryName: String = "",
    val description: String = "",
    val date: String = "",
    val walletIdError: String? = null,
    val typeError: String? = null,
    val amountError: String? = null,
    val dateError: String? = null,
    val isLoading: Boolean = false,
)
