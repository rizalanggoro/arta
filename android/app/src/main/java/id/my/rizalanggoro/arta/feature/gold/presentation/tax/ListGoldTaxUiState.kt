package id.my.rizalanggoro.arta.feature.gold.presentation.tax

import id.my.rizalanggoro.arta.openapi.models.DtoGoldTaxPreference

data class ListGoldTaxUiState(
    val preferences: List<DtoGoldTaxPreference> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,

    // delete
    val isDeleting: Boolean = false,
    val deleteTarget: DtoGoldTaxPreference? = null,
)