package id.my.rizalanggoro.arta.core.application.entry

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import id.my.rizalanggoro.arta.core.application.route.OtherRoute
import id.my.rizalanggoro.arta.feature.update.presentation.check.CheckUpdateScreen

fun EntryProviderScope<NavKey>.otherEntry() {
    entry<OtherRoute.CheckUpdate> {
        CheckUpdateScreen()
    }
}
