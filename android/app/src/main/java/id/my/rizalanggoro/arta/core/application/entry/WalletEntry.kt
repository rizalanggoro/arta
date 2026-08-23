package id.my.rizalanggoro.arta.core.application.entry

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import id.my.rizalanggoro.arta.core.application.route.WalletRoute
import id.my.rizalanggoro.arta.feature.wallet.presentation.createfirst.CreateFirstWalletScreen
import id.my.rizalanggoro.arta.feature.wallet.presentation.delete.DeleteWalletDialog
import id.my.rizalanggoro.arta.feature.wallet.presentation.delete.DeleteWalletVM
import id.my.rizalanggoro.arta.feature.wallet.presentation.list.ListWalletScreen
import id.my.rizalanggoro.arta.feature.wallet.presentation.select.SelectWalletScreen
import id.my.rizalanggoro.arta.feature.wallet.presentation.upsert.UpsertWalletScreen
import id.my.rizalanggoro.arta.shared.component.BottomSheetSceneStrategy

fun EntryProviderScope<NavKey>.walletEntry() {
    entry<WalletRoute.List> {
        ListWalletScreen()
    }

    entry<WalletRoute.Select>(
        metadata = BottomSheetSceneStrategy.bottomSheet()
    ) {
        SelectWalletScreen()
    }

    entry<WalletRoute.Upsert>(
        metadata = BottomSheetSceneStrategy.bottomSheet()
    ) {
        UpsertWalletScreen(
            walletId = it.walletId
        )
    }

    entry<WalletRoute.CreateFirst> {
        CreateFirstWalletScreen()
    }

    entry<WalletRoute.Delete>(
        metadata = DialogSceneStrategy.dialog()
    ) { navKey ->
        DeleteWalletDialog(
            vm = hiltViewModel<DeleteWalletVM, DeleteWalletVM.Factory>(
                creationCallback = {
                    it.create(
                        navKey = navKey
                    )
                }
            )
        )
    }
}
