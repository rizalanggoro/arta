package id.my.rizalanggoro.arta.feature.home.presentation.home

import id.my.rizalanggoro.arta.domain.Wallet

data class HomeUiState(
    val selectedIndex: Int = 0,
    val selectedWallet: Wallet? = null
)