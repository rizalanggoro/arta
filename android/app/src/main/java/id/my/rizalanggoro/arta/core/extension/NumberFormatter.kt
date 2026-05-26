package id.my.rizalanggoro.arta.core.extension

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

fun BigDecimal.toIndonesianCurrency(): String = NumberFormat
    .getCurrencyInstance(
        Locale.forLanguageTag(
            "id-ID"
        )
    )
    .format(this)

fun Int.toIndonesianCurrency(): String = NumberFormat
    .getCurrencyInstance(
        Locale.forLanguageTag(
            "id-ID"
        )
    )
    .format(this)

fun BigDecimal.toAmericanCurrency(): String = NumberFormat
    .getCurrencyInstance(
        Locale.forLanguageTag(
            "us-US"
        )
    )
    .format(this)