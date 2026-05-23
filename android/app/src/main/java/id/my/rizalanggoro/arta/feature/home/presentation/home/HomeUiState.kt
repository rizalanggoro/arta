package id.my.rizalanggoro.arta.feature.home.presentation.home

import id.my.rizalanggoro.arta.openapi.models.DomainWallet

data class HomeUiState(
    val selectedIndex: Int = 0,
    val selectedWallet: DomainWallet? = null
)