package id.my.rizalanggoro.arta.feature.category.presentation.upsert

data class UpsertCategoryUiState(
    val isUpdate: Boolean = false,
    val categoryId: Int = 0,
    val isLoading: Boolean = false,
    val name: String = "",
    val nameError: String? = null,
    val type: String = "expense",
    val errorMessage: String? = null,
)