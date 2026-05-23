package id.my.rizalanggoro.arta.feature.gold.presentation.tax

import id.my.rizalanggoro.arta.openapi.models.DtoGoldTaxPreference

data class ListGoldTaxUiState(
    val preferences: List<DtoGoldTaxPreference> = emptyList(),
    val isLoading: Boolean = false,
    val deleteTarget: DtoGoldTaxPreference? = null,
    val errorMessage: String? = null,
)