package id.my.rizalanggoro.arta.feature.category.presentation.update

data class UpdateCategoryUiState(
	val categoryId: Int? = null,
	val name: String = "",
	val type: String = "expense",
	val icon: String = "🧾",
	val color: String = "#E11D48",
	val nameError: String? = null,
	val errorMessage: String? = null,
	val isLoading: Boolean = false,
)