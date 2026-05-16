package id.my.rizalanggoro.arta.feature.category.presentation.update

data class UpdateCategoryUiState(
	val categoryId: Int? = null,
	val name: String = "",
	val type: String = "expense",
	val nameError: String? = null,
	val errorMessage: String? = null,
	val isLoading: Boolean = false,
)