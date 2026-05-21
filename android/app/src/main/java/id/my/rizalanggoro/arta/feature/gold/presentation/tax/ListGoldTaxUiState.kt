package id.my.rizalanggoro.arta.feature.gold.presentation.tax

import id.my.rizalanggoro.arta.domain.GoldTaxPreference

data class ListGoldTaxUiState(
	val preferences: List<GoldTaxPreference> = emptyList(),
	val deleteTarget: GoldTaxPreference? = null,
	val isLoading: Boolean = true,
	val errorMessage: String? = null,
)