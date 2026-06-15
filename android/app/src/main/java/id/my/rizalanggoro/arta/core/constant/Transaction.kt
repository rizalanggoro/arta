package id.my.rizalanggoro.arta.core.constant

enum class TransactionTimeRangeType {
    DAILY,
    WEEKLY,
    MONTHLY,
}

enum class TransactionGroupType {
    CATEGORY,
    TRANSACTION,
}

data class TransactionTimeRange(
    val title: String,
    val value: TransactionTimeRangeType,
)

data class TransactionGroup(
    val title: String,
    val value: TransactionGroupType,
)

val transactionTimeRanges = listOf(
    TransactionTimeRange("Harian", TransactionTimeRangeType.DAILY),
    TransactionTimeRange("Mingguan", TransactionTimeRangeType.WEEKLY),
    TransactionTimeRange("Bulanan", TransactionTimeRangeType.MONTHLY),
)

val transactionGroups = listOf(
    TransactionGroup("Kategori", TransactionGroupType.CATEGORY),
    TransactionGroup("Transaksi", TransactionGroupType.TRANSACTION),
)