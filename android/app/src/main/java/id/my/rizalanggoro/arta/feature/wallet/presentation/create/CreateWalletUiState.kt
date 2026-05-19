package id.my.rizalanggoro.arta.feature.wallet.presentation.create

data class CreateWalletUiState(
    val name: String = "",
    val type: String = "cash_savings",
    val nameError: String? = null,
    val isLoading: Boolean = false,
)