package id.my.rizalanggoro.arta.feature.gold.presentation.upsert

import id.my.rizalanggoro.arta.openapi.models.DomainWallet

data class UpsertGoldUiState(
    val goldId: Int = 0,
    val isUpdate: Boolean = false,
    val selectedWallet: DomainWallet? = null,
    val date: Long = System.currentTimeMillis(),
    val grams: String = "",
    val price: String = "",
    val type: String = "pure_gold",
    val carat: String = "",
    val notes: String = "",
    val gramsError: String? = null,
    val priceError: String? = null,
    val caratError: String? = null,
    val isLoading: Boolean = false,
    val isDatePickerOpen: Boolean = false,
)
