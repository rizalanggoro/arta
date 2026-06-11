package id.my.rizalanggoro.arta.core.application.entry

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import id.my.rizalanggoro.arta.core.application.route.OtherRoute
import id.my.rizalanggoro.arta.feature.update.presentation.check.CheckUpdateScreen

@OptIn(ExperimentalMaterial3Api::class)
fun EntryProviderScope<NavKey>.otherEntry() {
    entry<OtherRoute.CheckUpdate> {
        CheckUpdateScreen()
    }
}