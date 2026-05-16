package id.my.rizalanggoro.arta.feature.wallet.presentation.select

import id.my.rizalanggoro.arta.domain.Wallet

data class SelectWalletUiState(
    val wallets: List<Wallet> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)