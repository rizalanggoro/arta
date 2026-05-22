package id.my.rizalanggoro.arta.feature.wallet.presentation.list

import id.my.rizalanggoro.arta.openapi.models.DtoWallet
import id.my.rizalanggoro.arta.openapi.models.DomainWallet

data class ListWalletUiState(
    val wallets: List<DtoWallet> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val deleteTarget: DomainWallet? = null,
    val selectedWallet: DomainWallet? = null,
)
