package id.my.rizalanggoro.arta.feature.gold.presentation.upserttax

data class UpsertGoldTaxUiState(
    val isUpdate: Boolean = false,
    val isLoading: Boolean = false,
    val carat: String = "",
    val caratError: String? = null,
    val taxRate: String = "",
    val taxRateError: String? = null,
    val errorMessage: String? = null,
)