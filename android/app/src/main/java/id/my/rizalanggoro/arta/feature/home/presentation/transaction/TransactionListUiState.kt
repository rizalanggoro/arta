package id.my.rizalanggoro.arta.feature.home.presentation.transaction

import id.my.rizalanggoro.arta.openapi.models.DomainTransaction

data class TransactionListUiState(
    val title: String = "Daftar Transaksi",
    val description: String = "Semua transaksi wallet tabungan uang akan tampil di sini.",
    val transactions: List<DomainTransaction> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
