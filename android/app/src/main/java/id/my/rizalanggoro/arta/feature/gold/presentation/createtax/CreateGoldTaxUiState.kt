package id.my.rizalanggoro.arta.feature.gold.presentation.createtax

data class CreateGoldTaxUiState(
	val carat: String = "",
	val taxRate: String = "",
	val caratError: String? = null,
	val taxRateError: String? = null,
	val isSaving: Boolean = false,
)