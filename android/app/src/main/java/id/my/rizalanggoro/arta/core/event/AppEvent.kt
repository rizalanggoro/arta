package id.my.rizalanggoro.arta.core.event

import id.my.rizalanggoro.arta.openapi.models.DomainCategory
import id.my.rizalanggoro.arta.openapi.models.DomainWallet

sealed class AppEvent {
    data object WalletChanged : AppEvent()
    data object CategoryChanged : AppEvent()
    data object TransactionChanged : AppEvent()
    data object GoldTaxChanged : AppEvent()

    data class CategorySelected(val category: DomainCategory) : AppEvent()
    data class WalletSelected(val wallet: DomainWallet) : AppEvent()
}