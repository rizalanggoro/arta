package id.my.rizalanggoro.arta.core.application

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import id.my.rizalanggoro.arta.core.application.entry.authEntry
import id.my.rizalanggoro.arta.core.application.entry.categoryEntry
import id.my.rizalanggoro.arta.core.application.entry.goldEntry
import id.my.rizalanggoro.arta.core.application.entry.homeEntry
import id.my.rizalanggoro.arta.core.application.entry.otherEntry
import id.my.rizalanggoro.arta.core.application.entry.transactionEntry
import id.my.rizalanggoro.arta.core.application.entry.walletEntry
import id.my.rizalanggoro.arta.core.application.route.AuthRoute
import id.my.rizalanggoro.arta.core.application.route.HomeRoute
import id.my.rizalanggoro.arta.core.application.route.WalletRoute
import id.my.rizalanggoro.arta.core.data.AuthPrefs
import id.my.rizalanggoro.arta.core.data.SelectedWalletPrefs
import id.my.rizalanggoro.arta.core.data.ThemePrefs
import id.my.rizalanggoro.arta.core.utils.LocalBackStack
import id.my.rizalanggoro.arta.shared.component.BottomSheetSceneStrategy
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposeApp(
    authPrefs: AuthPrefs,
    selectedWalletPrefs: SelectedWalletPrefs,
    themePrefs: ThemePrefs,
) {
    val session by authPrefs.currentSession.collectAsState()
    val selectedWallet by selectedWalletPrefs.selectedWallet.collectAsState()
    val isDarkTheme by themePrefs.isDarkTheme.collectAsState()

    val startRoute = when {
        session != null && selectedWallet == null -> WalletRoute.CreateFirst
        session != null -> HomeRoute.Index
        else -> AuthRoute.Login
    }

    val backStack = rememberNavBackStack(startRoute)
    val bottomSheetStrategy = remember { BottomSheetSceneStrategy<NavKey>() }
    val dialogStrategy = remember { DialogSceneStrategy<NavKey>() }

    ArtaTheme(darkTheme = isDarkTheme) {
        CompositionLocalProvider(LocalBackStack provides backStack) {
            Surface {
                NavDisplay(
                    backStack = backStack,
                    onBack = { backStack.removeLastOrNull() },
                    entryDecorators = listOf(
                        rememberSaveableStateHolderNavEntryDecorator(),
                        rememberViewModelStoreNavEntryDecorator(),
                    ),
                    sceneStrategies = listOf(
                        bottomSheetStrategy,
                        dialogStrategy
                    ),
                    transitionSpec = {
                        slideInHorizontally(
                            initialOffsetX = { it },
                        ) + fadeIn() togetherWith slideOutHorizontally(
                            targetOffsetX = { -it },
                        ) + fadeOut()
                    },
                    popTransitionSpec = {
                        slideInHorizontally(
                            initialOffsetX = { -it },
                        ) + fadeIn() togetherWith slideOutHorizontally(
                            targetOffsetX = { it },
                        ) + fadeOut()
                    },
                    predictivePopTransitionSpec = {
                        slideInHorizontally(initialOffsetX = { -it }
                        ) + fadeIn() togetherWith slideOutHorizontally(
                            targetOffsetX = { it },
                        ) + fadeOut()
                    },
                    entryProvider = entryProvider {
                        authEntry()
                        walletEntry()
                        categoryEntry()
                        transactionEntry()
                        goldEntry()
                        homeEntry()
                        otherEntry()
                    },
                )
            }
        }
    }
}
