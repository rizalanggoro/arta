package id.my.rizalanggoro.arta.feature.category.presentation.upsert

data class UpsertCategoryUiState(
    val isUpdate: Boolean = false,
    val isLoading: Boolean = false,
    val name: String = "",
    val nameError: String? = null,
    val type: String = "expense",
    val errorMessage: String? = null,
)