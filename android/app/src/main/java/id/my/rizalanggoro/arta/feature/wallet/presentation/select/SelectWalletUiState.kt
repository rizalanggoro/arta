package id.my.rizalanggoro.arta.feature.wallet.presentation.select

import id.my.rizalanggoro.arta.openapi.models.DtoWallet
import id.my.rizalanggoro.arta.openapi.models.DomainWallet

data class SelectWalletUiState(
    val selectedWallet: DomainWallet? = null,
    val wallets: List<DtoWallet> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)