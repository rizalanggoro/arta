package id.my.rizalanggoro.arta.core.constant

import id.my.rizalanggoro.arta.core.extension.toFormattedDate
import id.my.rizalanggoro.arta.core.extension.toIndonesianDate
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters

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

fun calculateTimeRange(
    type: TransactionTimeRangeType,
    offset: Int,
): Pair<Long, Long> {
    val now = LocalDate.now()
    val shifted = offset.toLong()

    val start = when (type) {
        TransactionTimeRangeType.DAILY -> now
            .plusDays(shifted)
            .atStartOfDay(ZoneId.systemDefault())

        TransactionTimeRangeType.WEEKLY -> now
            .plusWeeks(shifted)
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            .atStartOfDay(ZoneId.systemDefault())

        TransactionTimeRangeType.MONTHLY -> now
            .plusMonths(shifted)
            .withDayOfMonth(1)
            .atStartOfDay(ZoneId.systemDefault())
    }

    val end = when (type) {
        TransactionTimeRangeType.DAILY -> start.plusDays(1)
        TransactionTimeRangeType.WEEKLY -> start.plusWeeks(1)
        TransactionTimeRangeType.MONTHLY -> start.plusMonths(1)
    }

    return start.toInstant().toEpochMilli() to end.toInstant().toEpochMilli()
}

fun formatTimeRangeLabel(
    type: TransactionTimeRangeType,
    startDateMillis: Long,
    endDateMillis: Long,
): String = when (type) {
    TransactionTimeRangeType.DAILY -> startDateMillis.toIndonesianDate()

    TransactionTimeRangeType.WEEKLY -> {
        val end = endDateMillis - 86400000
        val startMonth = startDateMillis.toFormattedDate("MMMM yyyy")
        val endMonth = end.toFormattedDate("MMMM yyyy")
        when {
            startMonth == endMonth ->
                "${startDateMillis.toFormattedDate("d")} - ${end.toFormattedDate("d MMMM yyyy")}"

            else ->
                "${startDateMillis.toFormattedDate("d MMMM")} - ${end.toFormattedDate("d MMMM yyyy")}"
        }
    }

    TransactionTimeRangeType.MONTHLY -> startDateMillis.toFormattedDate("MMMM yyyy")
}