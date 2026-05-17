package id.my.rizalanggoro.arta.feature.wallet.presentation.update

data class UpdateWalletUiState(
	val walletId: Int? = null,
	val name: String = "",
	val type: String = "cash_savings",
	
	val nameError: String? = null,
	val typeError: String? = null,
	val errorMessage: String? = null,
	val isLoading: Boolean = false,
)
