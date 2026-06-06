package id.my.rizalanggoro.arta.core.application.entry

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import id.my.rizalanggoro.arta.core.application.Routes
import id.my.rizalanggoro.arta.core.application.Routes.ActionSheetWalletRoute
import id.my.rizalanggoro.arta.core.application.Routes.CreateFirstWalletRoute
import id.my.rizalanggoro.arta.core.application.Routes.ListWalletRoute
import id.my.rizalanggoro.arta.core.application.Routes.SelectWalletRoute
import id.my.rizalanggoro.arta.core.application.Routes.UpsertWalletRoute
import id.my.rizalanggoro.arta.feature.wallet.presentation.action.ActionSheetWallet
import id.my.rizalanggoro.arta.feature.wallet.presentation.createfirst.CreateFirstWalletScreen
import id.my.rizalanggoro.arta.feature.wallet.presentation.delete.DeleteWalletDialog
import id.my.rizalanggoro.arta.feature.wallet.presentation.delete.DeleteWalletVM
import id.my.rizalanggoro.arta.feature.wallet.presentation.list.ListWalletScreen
import id.my.rizalanggoro.arta.feature.wallet.presentation.select.SelectWalletScreen
import id.my.rizalanggoro.arta.feature.wallet.presentation.upsert.UpsertWalletScreen
import id.my.rizalanggoro.arta.shared.component.BottomSheetSceneStrategy

@OptIn(ExperimentalMaterial3Api::class)
fun EntryProviderScope<NavKey>.walletEntry() {
    entry<ListWalletRoute> {
        ListWalletScreen()
    }

    entry<SelectWalletRoute>(
        metadata = BottomSheetSceneStrategy.bottomSheet()
    ) {
        SelectWalletScreen()
    }

    entry<UpsertWalletRoute>(
        metadata = BottomSheetSceneStrategy.bottomSheet()
    ) {
        UpsertWalletScreen(
            walletId = it.walletId
        )
    }

    entry<CreateFirstWalletRoute> {
        CreateFirstWalletScreen()
    }

    entry<ActionSheetWalletRoute>(
        metadata = BottomSheetSceneStrategy.bottomSheet()
    ) {
        ActionSheetWallet(
            walletId = it.walletId
        )
    }

    entry<Routes.DeleteWalletRoute>(
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