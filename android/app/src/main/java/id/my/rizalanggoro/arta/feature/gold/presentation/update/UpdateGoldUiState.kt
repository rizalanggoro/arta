package id.my.rizalanggoro.arta.feature.gold.presentation.update

data class UpdateGoldUiState(
    val id: Int? = null,
    val date: String = "",
    val grams: String = "",
    val pricePerGram: String = "",
    val type: String = "",
    val purityPercent: String = "",
    val notes: String = "",
    val isLoading: Boolean = false,
)
