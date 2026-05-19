package id.my.rizalanggoro.arta.core.event

import id.my.rizalanggoro.arta.domain.Category
import id.my.rizalanggoro.arta.domain.Wallet

sealed class AppEvent {
    data object TransactionChanged : AppEvent()
    data object WalletChanged : AppEvent()

    data class CategorySelected(val category: Category) : AppEvent()
    data class WalletSelected(val wallet: Wallet) : AppEvent()
}