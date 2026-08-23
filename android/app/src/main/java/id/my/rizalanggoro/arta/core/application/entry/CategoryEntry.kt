package id.my.rizalanggoro.arta.core.application.entry

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import id.my.rizalanggoro.arta.core.application.route.CategoryRoute
import id.my.rizalanggoro.arta.feature.category.presentation.delete.DeleteCategoryDialog
import id.my.rizalanggoro.arta.feature.category.presentation.delete.DeleteCategoryVM
import id.my.rizalanggoro.arta.feature.category.presentation.detail.DetailCategoryScreen
import id.my.rizalanggoro.arta.feature.category.presentation.detail.DetailCategoryVM
import id.my.rizalanggoro.arta.feature.category.presentation.list.ListCategoryScreen
import id.my.rizalanggoro.arta.feature.category.presentation.select.SelectCategoryScreen
import id.my.rizalanggoro.arta.feature.category.presentation.select.SelectCategoryVM
import id.my.rizalanggoro.arta.feature.category.presentation.upsert.UpsertCategoryScreen
import id.my.rizalanggoro.arta.feature.category.presentation.upsert.UpsertCategoryVM
import id.my.rizalanggoro.arta.shared.component.BottomSheetSceneStrategy

fun EntryProviderScope<NavKey>.categoryEntry() {
    entry<CategoryRoute.List> {
        ListCategoryScreen()
    }

    entry<CategoryRoute.Detail> { navKey ->
        DetailCategoryScreen(
            vm = hiltViewModel<DetailCategoryVM, DetailCategoryVM.Factory>(
                creationCallback = {
                    it.create(
                        navKey = navKey
                    )
                }
            )
        )
    }

    entry<CategoryRoute.Select>(
        metadata = BottomSheetSceneStrategy.bottomSheet()
    ) { navKey ->
        SelectCategoryScreen(
            selectedCategoryId = navKey.categoryId,
            vm = hiltViewModel<SelectCategoryVM, SelectCategoryVM.Factory>(
                creationCallback = {
                    it.create(
                        navKey = navKey
                    )
                }
            )
        )
    }

    entry<CategoryRoute.Upsert>(
        metadata = BottomSheetSceneStrategy.bottomSheet()
    ) { navKey ->
        UpsertCategoryScreen(
            vm = hiltViewModel<UpsertCategoryVM, UpsertCategoryVM.Factory>(
                creationCallback = {
                    it.create(
                        navKey = navKey
                    )
                }
            )
        )
    }

    entry<CategoryRoute.Delete>(
        metadata = DialogSceneStrategy.dialog()
    ) { navKey ->
        DeleteCategoryDialog(
            vm = hiltViewModel<DeleteCategoryVM, DeleteCategoryVM.Factory>(
                creationCallback = {
                    it.create(
                        navKey = navKey
                    )
                }
            )
        )
    }
}
