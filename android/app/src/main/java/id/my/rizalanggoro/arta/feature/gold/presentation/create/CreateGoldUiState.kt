package id.my.rizalanggoro.arta.feature.gold.presentation.create

import id.my.rizalanggoro.arta.domain.Wallet

data class CreateGoldUiState(
    val selectedWallet: Wallet? = null,
    val date: String = java.time.OffsetDateTime.now().toString(),
    val grams: String = "",
    val price: String = "",
    val type: String = "pure_gold",
    val carat: String = "24.0",
    val notes: String = "",
    val dateError: String? = null,
    val gramsError: String? = null,
    val priceError: String? = null,
    val caratError: String? = null,
    val isLoading: Boolean = false,
)
