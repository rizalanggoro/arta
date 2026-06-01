package id.my.rizalanggoro.arta.core.application.entry

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import id.my.rizalanggoro.arta.core.application.Routes.HomeGoldRoute
import id.my.rizalanggoro.arta.core.application.Routes.HomeRoute
import id.my.rizalanggoro.arta.core.application.Routes.HomeSettingRoute
import id.my.rizalanggoro.arta.core.application.Routes.HomeTransactionRoute
import id.my.rizalanggoro.arta.core.application.Routes.UpdateRoute
import id.my.rizalanggoro.arta.feature.home.presentation.gold.HomeGoldScreen
import id.my.rizalanggoro.arta.feature.home.presentation.home.HomeScreen
import id.my.rizalanggoro.arta.feature.home.presentation.setting.HomeSettingScreen
import id.my.rizalanggoro.arta.feature.home.presentation.transaction.HomeTransactionScreen
import id.my.rizalanggoro.arta.feature.update.presentation.check.CheckUpdateScreen

@OptIn(ExperimentalMaterial3Api::class)
fun EntryProviderScope<NavKey>.homeEntry() {
    entry<HomeGoldRoute> {
        HomeGoldScreen()
    }

    entry<HomeRoute> {
        HomeScreen()
    }

    entry<HomeTransactionRoute> {
        HomeTransactionScreen()
    }

    entry<HomeSettingRoute> {
        HomeSettingScreen()
    }

    entry<UpdateRoute> {
        CheckUpdateScreen()
    }
}