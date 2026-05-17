package id.my.rizalanggoro.arta.feature.home.presentation.transaction

data class TransactionListUiState(
    val title: String = "Daftar Transaksi",
    val description: String = "Semua transaksi wallet tabungan uang akan tampil di sini.",
    val transactions: List<id.my.rizalanggoro.arta.domain.Transaction> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
