package id.my.rizalanggoro.arta.feature.wallet.presentation.list

import id.my.rizalanggoro.arta.openapi.models.DomainWallet
import id.my.rizalanggoro.arta.openapi.models.DtoWallet

data class ListWalletUiState(
    val wallets: List<DtoWallet> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedWallet: DomainWallet? = null,

    // delete
    val isDeleting: Boolean = false,
    val deleteTarget: DomainWallet? = null,
)
