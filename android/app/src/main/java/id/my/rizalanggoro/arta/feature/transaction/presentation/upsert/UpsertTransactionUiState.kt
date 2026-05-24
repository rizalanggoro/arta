package id.my.rizalanggoro.arta.feature.transaction.presentation.upsert

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

fun currentIsoDate(): String {
    return OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
}

data class UpsertTransactionUiState(
    val transactionId: Int = 0,
    val isUpdate: Boolean = false,
    val walletId: String = "",
    val selectedWalletName: String = "",
    val amount: String = "",
    val categoryId: String = "",
    val selectedCategoryName: String = "",
    val description: String = "",
    val date: String = currentIsoDate(),
    val walletIdError: String? = null,
    val amountError: String? = null,
    val categoryError: String? = null,
    val dateError: String? = null,
    val isLoading: Boolean = false,
)