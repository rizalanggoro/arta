package id.my.rizalanggoro.arta.core.event

import id.my.rizalanggoro.arta.openapi.models.DomainCategory
import id.my.rizalanggoro.arta.openapi.models.DomainWallet

sealed class AppEvent {
    data object WalletChanged : AppEvent()
    data object CategoryChanged : AppEvent()
    data object TransactionChanged : AppEvent()
    data object GoldChanged : AppEvent()
    data object GoldTaxChanged : AppEvent()

    data class CategorySelected(val category: DomainCategory) : AppEvent()
    data class WalletSelected(val wallet: DomainWallet) : AppEvent()

    data object WalletActionSheet {
        data object OnDismissClicked : AppEvent()
        data object OnEditClicked : AppEvent()
        data object OnDeleteClicked : AppEvent()
    }

    data object CategoryActionSheet {
        data object OnDismissClicked : AppEvent()
        data object OnEditClicked : AppEvent()
        data object OnDeleteClicked : AppEvent()
    }

    data object TransactionActionSheet {
        data object OnDismissClicked : AppEvent()
        data object OnEditClicked : AppEvent()
        data object OnDeleteClicked : AppEvent()
    }

    data object GoldActionSheet {
        data object OnDismissClicked : AppEvent()
        data object OnEditClicked : AppEvent()
        data object OnDeleteClicked : AppEvent()
    }

    data object GoldTaxActionSheet {
        data object OnDismissClicked : AppEvent()
        data object OnEditClicked : AppEvent()
        data object OnDeleteClicked : AppEvent()
    }
}