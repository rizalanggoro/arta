package id.my.rizalanggoro.arta.core.extension

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun Long.toIndonesianDate(): String {
    val formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale.forLanguageTag("id-ID"))

    val localDate = Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()

    return localDate.format(formatter).replaceFirstChar { it.uppercase() }
}

fun Long.toApiFormat(): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    return Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(formatter)
}