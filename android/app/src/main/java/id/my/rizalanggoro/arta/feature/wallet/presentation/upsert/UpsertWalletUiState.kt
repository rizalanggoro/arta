package id.my.rizalanggoro.arta.feature.wallet.presentation.upsert

data class UpsertWalletUiState(
    val walletId: Int = 0,
    val name: String = "",
    val type: String = "cash_savings",
    val nameError: String? = null,
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
)
