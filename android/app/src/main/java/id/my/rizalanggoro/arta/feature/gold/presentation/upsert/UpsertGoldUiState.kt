package id.my.rizalanggoro.arta.feature.gold.presentation.upsert

import id.my.rizalanggoro.arta.openapi.models.DomainWallet
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

fun currentIsoDate(): String {
    return OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
}

data class UpsertGoldUiState(
    val goldId: Int = 0,
    val isUpdate: Boolean = false,
    val selectedWallet: DomainWallet? = null,
    val date: String = currentIsoDate(),
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
