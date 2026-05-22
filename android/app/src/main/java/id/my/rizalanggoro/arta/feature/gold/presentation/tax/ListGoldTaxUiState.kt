package id.my.rizalanggoro.arta.feature.gold.presentation.tax

import id.my.rizalanggoro.arta.domain.GoldTaxPreference

data class ListGoldTaxUiState(
    val preferences: List<GoldTaxPreference> = emptyList(),
    val isLoading: Boolean = false,
    val deleteTarget: GoldTaxPreference? = null,
    val errorMessage: String? = null,
)