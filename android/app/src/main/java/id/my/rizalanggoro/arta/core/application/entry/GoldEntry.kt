package id.my.rizalanggoro.arta.core.application.entry

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import id.my.rizalanggoro.arta.core.application.route.GoldRoute
import id.my.rizalanggoro.arta.feature.gold.presentation.action.GoldActionSheet
import id.my.rizalanggoro.arta.feature.gold.presentation.delete.DeleteGoldDialog
import id.my.rizalanggoro.arta.feature.gold.presentation.delete.DeleteGoldVM
import id.my.rizalanggoro.arta.feature.gold.presentation.detail.GoldDetailScreen
import id.my.rizalanggoro.arta.feature.gold.presentation.tax.ListGoldTaxScreen
import id.my.rizalanggoro.arta.feature.gold.presentation.upsert.UpsertGoldScreen
import id.my.rizalanggoro.arta.feature.gold.presentation.upserttax.UpsertGoldTaxScreen
import id.my.rizalanggoro.arta.shared.component.BottomSheetSceneStrategy

@OptIn(ExperimentalMaterial3Api::class)
fun EntryProviderScope<NavKey>.goldEntry() {
    entry<GoldRoute.Upsert> {
        UpsertGoldScreen(
            goldId = it.goldId
        )
    }

    entry<GoldRoute.ListTax> {
        ListGoldTaxScreen()
    }

    entry<GoldRoute.UpsertTax>(
        metadata = BottomSheetSceneStrategy.bottomSheet()
    ) {
        UpsertGoldTaxScreen(
            taxPreferenceId = it.id
        )
    }

    entry<GoldRoute.Detail> {
        GoldDetailScreen(
            goldId = it.id
        )
    }

    entry<GoldRoute.ActionSheet>(
        metadata = BottomSheetSceneStrategy.bottomSheet()
    ) {
        GoldActionSheet(
            goldId = it.goldId
        )
    }

    entry<GoldRoute.Delete>(
        metadata = DialogSceneStrategy.dialog()
    ) { navKey ->
        DeleteGoldDialog(
            vm = hiltViewModel<DeleteGoldVM, DeleteGoldVM.Factory>(
                creationCallback = {
                    it.create(
                        navKey = navKey
                    )
                }
            )
        )
    }
}