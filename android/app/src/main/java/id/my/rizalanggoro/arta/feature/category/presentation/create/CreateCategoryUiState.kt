package id.my.rizalanggoro.arta.feature.category.presentation.create

data class CreateCategoryUiState(
	val name: String = "",
	val type: String = "expense",
	val icon: String = "🧾",
	val color: String = "#E11D48",
	val nameError: String? = null,
	val isLoading: Boolean = false,
)