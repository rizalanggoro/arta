package id.my.rizalanggoro.arta.feature.gold.presentation.create

data class CreateGoldUiState(
	val walletId: String = "",
	val date: String = java.time.OffsetDateTime.now().toString(),
	val grams: String = "",
	val pricePerGram: String = "",
	val type: String = "pure_gold",
	val purityPercent: String = "99.9",
	val notes: String = "",
	val walletIdError: String? = null,
	val dateError: String? = null,
	val gramsError: String? = null,
	val pricePerGramError: String? = null,
	val purityPercentError: String? = null,
	val isLoading: Boolean = false,
)
