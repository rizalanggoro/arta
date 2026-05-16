package id.my.rizalanggoro.arta.feature.wallet.presentation.list

import id.my.rizalanggoro.arta.domain.Wallet

data class ListWalletUiState(
	val wallets: List<Wallet> = emptyList(),
	val isLoading: Boolean = false,
	val errorMessage: String? = null,
	val deleteTarget: Wallet? = null,
)
