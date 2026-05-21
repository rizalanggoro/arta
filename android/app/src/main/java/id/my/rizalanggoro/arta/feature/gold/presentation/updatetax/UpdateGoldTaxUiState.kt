package id.my.rizalanggoro.arta.feature.gold.presentation.updatetax

import id.my.rizalanggoro.arta.domain.GoldTaxPreference

data class UpdateGoldTaxUiState(
	val preferences: List<GoldTaxPreference> = emptyList(),
	val carat: String = "",
	val taxRate: String = "",
	val caratError: String? = null,
	val taxRateError: String? = null,
	val isLoading: Boolean = true,
	val isSaving: Boolean = false,
	val errorMessage: String? = null,
)