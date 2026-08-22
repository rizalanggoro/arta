package id.my.rizalanggoro.arta.feature.category.presentation.list

import id.my.rizalanggoro.arta.openapi.models.DtoCategory

data class ListCategoryUiState(
    val incomeCategories: List<DtoCategory> = emptyList(),
    val expenseCategories: List<DtoCategory> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedType: String = "income",
)
