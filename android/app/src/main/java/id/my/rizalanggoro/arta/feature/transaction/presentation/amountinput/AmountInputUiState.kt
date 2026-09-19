package id.my.rizalanggoro.arta.feature.transaction.presentation.amountinput

data class AmountInputUiState(
    val expression: String = "",
    val result: Double? = null,
    val canConfirm: Boolean = false,
)
