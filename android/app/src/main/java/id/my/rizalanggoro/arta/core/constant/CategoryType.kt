package id.my.rizalanggoro.arta.core.constant

data class CategoryType(
    val name: String,
    val value: String
)

val categoryTypes = listOf(
    CategoryType(name = "Pemasukan", value = "income"),
    CategoryType(name = "Pengeluaran", value = "expense"),
)