package id.my.rizalanggoro.arta.core.application.entry

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import id.my.rizalanggoro.arta.core.application.Routes
import id.my.rizalanggoro.arta.core.application.Routes.TransactionDetailRoute
import id.my.rizalanggoro.arta.core.application.Routes.TransactionUpsertRoute
import id.my.rizalanggoro.arta.feature.transaction.presentation.action.TransactionActionSheet
import id.my.rizalanggoro.arta.feature.transaction.presentation.delete.DeleteTransactionDialog
import id.my.rizalanggoro.arta.feature.transaction.presentation.delete.DeleteTransactionVM
import id.my.rizalanggoro.arta.feature.transaction.presentation.detail.TransactionDetailScreen
import id.my.rizalanggoro.arta.feature.transaction.presentation.upsert.UpsertTransactionScreen
import id.my.rizalanggoro.arta.feature.transaction.presentation.upsert.UpsertTransactionVM
import id.my.rizalanggoro.arta.shared.component.BottomSheetSceneStrategy

@OptIn(ExperimentalMaterial3Api::class)
fun EntryProviderScope<NavKey>.transactionEntry() {
    entry<TransactionUpsertRoute> { navKey ->
        UpsertTransactionScreen(
            vm = hiltViewModel<UpsertTransactionVM, UpsertTransactionVM.Factory>(
                creationCallback = {
                    it.create(
                        navKey = navKey
                    )
                }
            )
        )
    }

    entry<TransactionDetailRoute> {
        TransactionDetailScreen(
            transactionId = it.id
        )
    }

    entry<Routes.TransactionActionSheetRoute>(
        metadata = BottomSheetSceneStrategy.bottomSheet()
    ) {
        TransactionActionSheet(
            transactionId = it.transactionId
        )
    }

    entry<Routes.DeleteTransactionRoute>(
        metadata = DialogSceneStrategy.dialog()
    ) { navKey ->
        DeleteTransactionDialog(
            vm = hiltViewModel<DeleteTransactionVM, DeleteTransactionVM.Factory>(
                creationCallback = {
                    it.create(
                        navKey = navKey
                    )
                }
            )
        )
    }
}