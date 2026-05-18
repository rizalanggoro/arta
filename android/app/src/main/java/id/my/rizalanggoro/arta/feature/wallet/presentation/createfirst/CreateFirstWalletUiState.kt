package id.my.rizalanggoro.arta.feature.wallet.presentation.createfirst

data class CreateFirstWalletUiState(
	val name: String = "",
	val type: String = "",
	val nameError: String? = null,
	val typeError: String? = null,
	val isLoading: Boolean = false,
)