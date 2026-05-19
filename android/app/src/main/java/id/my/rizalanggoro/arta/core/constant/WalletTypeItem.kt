package id.my.rizalanggoro.arta.core.constant

data class WalletTypeItem(
    val name: String,
    val value: String,
)

val walletTypes = listOf(
    WalletTypeItem(name = "Tabungan Uang", value = "cash_savings"),
    WalletTypeItem(name = "Tabungan Emas", value = "gold_savings"),
)