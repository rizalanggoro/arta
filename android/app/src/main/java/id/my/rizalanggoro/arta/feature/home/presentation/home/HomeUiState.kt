package id.my.rizalanggoro.arta.feature.home.presentation.home

data class HomeUiState(
	val walletType: HomeWalletType = HomeWalletType.CashSavings,
	val selectedIndex: Int = 0,
)

enum class HomeWalletType {
	CashSavings,
	GoldSavings,
}