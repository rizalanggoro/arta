package id.my.rizalanggoro.arta.core.constant

data class GoldType(
    val name: String,
    val value: String,
)

val goldTypes = listOf(
    GoldType(name = "Emas Murni", value = "pure_gold"),
    GoldType(name = "Perhiasan", value = "jewelry"),
)