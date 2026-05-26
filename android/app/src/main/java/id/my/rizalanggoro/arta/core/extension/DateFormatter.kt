package id.my.rizalanggoro.arta.core.extension

import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val formatter = DateTimeFormatter.ofPattern(
    "EEEE, d MMMM yyyy",
    Locale.forLanguageTag("id-ID")
)

private val apiFormatter = DateTimeFormatter.ofPattern(
    "yyyy-MM-dd"
)

fun Long.toIndonesianDate() = Instant
    .ofEpochMilli(this)
    .atZone(ZoneId.systemDefault())
    .toLocalDate()
    .format(formatter)
    .replaceFirstChar { it.uppercase() }

fun Long.toApiFormat(): String = Instant
    .ofEpochMilli(this)
    .atZone(ZoneId.systemDefault())
    .toLocalDate()
    .format(apiFormatter)

fun String.toIndonesianDate() = OffsetDateTime
    .parse(this)
    .toLocalDate()
    .format(formatter)
    .replaceFirstChar { it.uppercase() }