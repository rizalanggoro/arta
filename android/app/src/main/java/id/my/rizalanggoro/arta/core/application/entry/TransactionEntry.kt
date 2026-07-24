package id.my.rizalanggoro.arta.core.application.entry

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import id.my.rizalanggoro.arta.core.application.route.TransactionRoute
import id.my.rizalanggoro.arta.feature.transaction.presentation.action.TransactionActionSheet
import id.my.rizalanggoro.arta.feature.transaction.presentation.action.TransactionFilterActionSheet
import id.my.rizalanggoro.arta.feature.transaction.presentation.delete.DeleteTransactionDialog
import id.my.rizalanggoro.arta.feature.transaction.presentation.delete.DeleteTransactionVM
import id.my.rizalanggoro.arta.feature.transaction.presentation.detail.TransactionDetailScreen
import id.my.rizalanggoro.arta.feature.transaction.presentation.detail.TransactionDetailVM
import id.my.rizalanggoro.arta.feature.transaction.presentation.upsert.UpsertTransactionScreen
import id.my.rizalanggoro.arta.feature.transaction.presentation.upsert.UpsertTransactionVM
import id.my.rizalanggoro.arta.shared.component.BottomSheetSceneStrategy

@OptIn(ExperimentalMaterial3Api::class)
fun EntryProviderScope<NavKey>.transactionEntry() {
    entry<TransactionRoute.Upsert> { navKey ->
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

    entry<TransactionRoute.Detail> { navKey ->
        TransactionDetailScreen(
            transactionId = navKey.transactionId,
            vm = hiltViewModel<TransactionDetailVM, TransactionDetailVM.Factory>(
                creationCallback = { it.create(transactionId = navKey.transactionId) }
            )
        )
    }

    entry<TransactionRoute.ActionSheet>(
        metadata = BottomSheetSceneStrategy.bottomSheet()
    ) {
        TransactionActionSheet(
            transactionId = it.transactionId
        )
    }

    entry<TransactionRoute.Delete>(
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

    entry<TransactionRoute.Filter>(
        metadata = BottomSheetSceneStrategy.bottomSheet()
    ) {
        TransactionFilterActionSheet()
    }
}