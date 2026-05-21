package id.my.rizalanggoro.arta.core.constant

data class WalletType(
    val name: String,
    val value: String,
)

val walletTypes = listOf(
    WalletType(name = "Tabungan Uang", value = "cash_savings"),
    WalletType(name = "Tabungan Emas", value = "gold_savings"),
)

fun String.toWalletName(): String = walletTypes
    .firstOrNull { it.value == this }?.name ?: this