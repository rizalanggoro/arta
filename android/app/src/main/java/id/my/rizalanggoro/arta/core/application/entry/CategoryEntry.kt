package id.my.rizalanggoro.arta.core.application.entry

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import id.my.rizalanggoro.arta.core.application.Routes.CategoryRoute
import id.my.rizalanggoro.arta.core.application.Routes.CategorySelectRoute
import id.my.rizalanggoro.arta.core.application.Routes.CategoryUpsertRoute
import id.my.rizalanggoro.arta.feature.category.presentation.list.ListCategoryScreen
import id.my.rizalanggoro.arta.feature.category.presentation.select.SelectCategoryScreen
import id.my.rizalanggoro.arta.feature.category.presentation.select.SelectCategoryVM
import id.my.rizalanggoro.arta.feature.category.presentation.upsert.UpsertCategoryScreen
import id.my.rizalanggoro.arta.feature.category.presentation.upsert.UpsertCategoryVM
import id.my.rizalanggoro.arta.shared.component.BottomSheetSceneStrategy

@OptIn(ExperimentalMaterial3Api::class)
fun EntryProviderScope<NavKey>.categoryEntry() {
    entry<CategoryRoute> {
        ListCategoryScreen()
    }

    entry<CategorySelectRoute>(
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

    entry<CategoryUpsertRoute>(
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
}