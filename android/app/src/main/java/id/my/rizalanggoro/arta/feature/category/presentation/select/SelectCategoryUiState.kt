package id.my.rizalanggoro.arta.feature.category.presentation.select

import id.my.rizalanggoro.arta.openapi.models.DtoCategory

data class SelectCategoryUiState(
    val categories: List<DtoCategory> = emptyList(),
    val selectedType: String = "income",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)