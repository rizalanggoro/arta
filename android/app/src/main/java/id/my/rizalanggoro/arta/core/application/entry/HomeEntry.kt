package id.my.rizalanggoro.arta.core.application.entry

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import id.my.rizalanggoro.arta.core.application.route.HomeRoute
import id.my.rizalanggoro.arta.feature.home.presentation.home.HomeScreen

fun EntryProviderScope<NavKey>.homeEntry() {
    entry<HomeRoute.Index> {
        HomeScreen()
    }
}
