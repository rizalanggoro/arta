package id.my.rizalanggoro.arta.feature.gold.presentation.update

data class UpdateGoldUiState(
    val id: Int? = null,
    val date: String = "",
    val grams: String = "",
    val price: String = "",
    val type: String = "",
    val carat: String = "",
    val notes: String = "",
    val isLoading: Boolean = false,
)
