package id.my.rizalanggoro.arta.core.event

import id.my.rizalanggoro.arta.openapi.models.DomainCategory
import id.my.rizalanggoro.arta.openapi.models.DomainWallet

sealed interface AppEvent {
    data object WalletChanged : AppEvent
    data object CategoryChanged : AppEvent
    data object TransactionChanged : AppEvent
    data object GoldChanged : AppEvent
    data object GoldTaxChanged : AppEvent

    data class CategorySelected(val category: DomainCategory) : AppEvent
    data class WalletSelected(val wallet: DomainWallet) : AppEvent

    @Deprecated("use navigation instead")
    data object CategoryActionSheet {
        data class OnEditClicked(val categoryId: Int) : AppEvent
        data class OnDeleteClicked(val categoryId: Int) : AppEvent
    }

    @Deprecated("use navigation instead")
    data object TransactionActionSheet {
        data class OnEditClicked(val transactionId: Int) : AppEvent
        data class OnDeleteClicked(val transactionId: Int) : AppEvent
    }
}