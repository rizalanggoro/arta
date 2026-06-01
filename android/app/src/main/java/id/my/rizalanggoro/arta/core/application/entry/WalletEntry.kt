package id.my.rizalanggoro.arta.core.application.entry

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import id.my.rizalanggoro.arta.core.application.Routes.UpsertWalletRoute
import id.my.rizalanggoro.arta.core.application.Routes.WalletCreateFirstRoute
import id.my.rizalanggoro.arta.core.application.Routes.WalletRoute
import id.my.rizalanggoro.arta.core.application.Routes.WalletSelectRoute
import id.my.rizalanggoro.arta.feature.wallet.presentation.createfirst.CreateFirstWalletScreen
import id.my.rizalanggoro.arta.feature.wallet.presentation.list.ListWalletScreen
import id.my.rizalanggoro.arta.feature.wallet.presentation.select.SelectWalletScreen
import id.my.rizalanggoro.arta.feature.wallet.presentation.upsert.UpsertWalletScreen
import id.my.rizalanggoro.arta.shared.component.BottomSheetSceneStrategy

@OptIn(ExperimentalMaterial3Api::class)
fun EntryProviderScope<NavKey>.walletEntry() {
    entry<WalletRoute> {
        ListWalletScreen()
    }

    entry<WalletSelectRoute>(
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

    entry<WalletCreateFirstRoute> {
        CreateFirstWalletScreen()
    }
}