package id.my.rizalanggoro.arta.feature.category.presentation.create

data class CreateCategoryUiState(
	val name: String = "",
	val type: String = "expense",
	val nameError: String? = null,
	val isLoading: Boolean = false,
)