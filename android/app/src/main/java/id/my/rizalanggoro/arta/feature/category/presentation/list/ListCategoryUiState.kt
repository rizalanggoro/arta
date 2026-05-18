package id.my.rizalanggoro.arta.feature.category.presentation.list

import id.my.rizalanggoro.arta.domain.Category

data class ListCategoryUiState(
	val categories: List<Category> = emptyList(),
	val isLoading: Boolean = false,
	val errorMessage: String? = null,
	val selectedType: String = "expense",
	val actionTarget: Category? = null,
	val deleteTarget: Category? = null,
)