package id.my.rizalanggoro.arta.feature.category.presentation.select

import id.my.rizalanggoro.arta.domain.Category

data class SelectCategoryUiState(
    val categories: List<Category> = emptyList(),
    val selectedType: String = "expense",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)