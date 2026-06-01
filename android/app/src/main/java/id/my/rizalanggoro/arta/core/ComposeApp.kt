package id.my.rizalanggoro.arta.core

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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import id.my.rizalanggoro.arta.core.Routes.CategoryRoute
import id.my.rizalanggoro.arta.core.Routes.CategorySelectRoute
import id.my.rizalanggoro.arta.core.Routes.CategoryUpsertRoute
import id.my.rizalanggoro.arta.core.Routes.ForgotPasswordRoute
import id.my.rizalanggoro.arta.core.Routes.GoldDetailRoute
import id.my.rizalanggoro.arta.core.Routes.GoldTaxListRoute
import id.my.rizalanggoro.arta.core.Routes.HomeGoldRoute
import id.my.rizalanggoro.arta.core.Routes.HomeRoute
import id.my.rizalanggoro.arta.core.Routes.HomeSettingRoute
import id.my.rizalanggoro.arta.core.Routes.HomeTransactionRoute
import id.my.rizalanggoro.arta.core.Routes.LoginRoute
import id.my.rizalanggoro.arta.core.Routes.RegisterRoute
import id.my.rizalanggoro.arta.core.Routes.TransactionDetailRoute
import id.my.rizalanggoro.arta.core.Routes.TransactionUpsertRoute
import id.my.rizalanggoro.arta.core.Routes.UpdateRoute
import id.my.rizalanggoro.arta.core.Routes.UpsertGoldRoute
import id.my.rizalanggoro.arta.core.Routes.UpsertGoldTaxRoute
import id.my.rizalanggoro.arta.core.Routes.UpsertWalletRoute
import id.my.rizalanggoro.arta.core.Routes.WalletCreateFirstRoute
import id.my.rizalanggoro.arta.core.Routes.WalletRoute
import id.my.rizalanggoro.arta.core.Routes.WalletSelectRoute
import id.my.rizalanggoro.arta.core.data.AuthPrefs
import id.my.rizalanggoro.arta.core.data.SelectedWalletPrefs
import id.my.rizalanggoro.arta.core.data.ThemePrefs
import id.my.rizalanggoro.arta.feature.auth.presentation.forgotpassword.ForgotPasswordScreen
import id.my.rizalanggoro.arta.feature.auth.presentation.login.LoginScreen
import id.my.rizalanggoro.arta.feature.auth.presentation.logout.LogoutDialog
import id.my.rizalanggoro.arta.feature.auth.presentation.register.RegisterScreen
import id.my.rizalanggoro.arta.feature.category.presentation.list.ListCategoryScreen
import id.my.rizalanggoro.arta.feature.category.presentation.select.SelectCategoryScreen
import id.my.rizalanggoro.arta.feature.category.presentation.select.SelectCategoryVM
import id.my.rizalanggoro.arta.feature.category.presentation.upsert.UpsertCategoryScreen
import id.my.rizalanggoro.arta.feature.category.presentation.upsert.UpsertCategoryVM
import id.my.rizalanggoro.arta.feature.gold.presentation.action.GoldActionSheet
import id.my.rizalanggoro.arta.feature.gold.presentation.delete.DeleteGoldDialog
import id.my.rizalanggoro.arta.feature.gold.presentation.delete.DeleteGoldVM
import id.my.rizalanggoro.arta.feature.gold.presentation.detail.GoldDetailScreen
import id.my.rizalanggoro.arta.feature.gold.presentation.tax.ListGoldTaxScreen
import id.my.rizalanggoro.arta.feature.gold.presentation.upsert.UpsertGoldScreen
import id.my.rizalanggoro.arta.feature.gold.presentation.upserttax.UpsertGoldTaxScreen
import id.my.rizalanggoro.arta.feature.home.presentation.gold.HomeGoldScreen
import id.my.rizalanggoro.arta.feature.home.presentation.home.HomeScreen
import id.my.rizalanggoro.arta.feature.home.presentation.setting.HomeSettingScreen
import id.my.rizalanggoro.arta.feature.home.presentation.transaction.HomeTransactionScreen
import id.my.rizalanggoro.arta.feature.transaction.presentation.detail.TransactionDetailScreen
import id.my.rizalanggoro.arta.feature.transaction.presentation.upsert.UpsertTransactionScreen
import id.my.rizalanggoro.arta.feature.transaction.presentation.upsert.UpsertTransactionVM
import id.my.rizalanggoro.arta.feature.update.presentation.check.CheckUpdateScreen
import id.my.rizalanggoro.arta.feature.wallet.presentation.createfirst.CreateFirstWalletScreen
import id.my.rizalanggoro.arta.feature.wallet.presentation.list.ListWalletScreen
import id.my.rizalanggoro.arta.feature.wallet.presentation.select.SelectWalletScreen
import id.my.rizalanggoro.arta.feature.wallet.presentation.upsert.UpsertWalletScreen
import id.my.rizalanggoro.arta.shared.component.BottomSheetSceneStrategy
import id.my.rizalanggoro.arta.shared.component.TransactionActionSheet
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposeApp(
    authPrefs: AuthPrefs,
    selectedWalletPrefs: SelectedWalletPrefs,
    themePrefs: ThemePrefs
) {
    val session by authPrefs.currentSession.collectAsState()
    val selectedWallet by selectedWalletPrefs.selectedWallet.collectAsState()
    val isDarkTheme by themePrefs.isDarkTheme.collectAsState()

    val startRoute = when {
        session != null && selectedWallet == null -> WalletCreateFirstRoute
        session != null -> HomeRoute
        else -> LoginRoute
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
                        // auth
                        entry<LoginRoute> { LoginScreen() }
                        entry<RegisterRoute> { RegisterScreen() }
                        entry<ForgotPasswordRoute> { ForgotPasswordScreen() }
                        entry<Routes.LogoutRoute>(
                            metadata = DialogSceneStrategy.dialog()
                        ) { LogoutDialog() }

                        // wallet
                        entry<WalletRoute> { ListWalletScreen() }
                        entry<WalletSelectRoute>(
                            metadata = BottomSheetSceneStrategy.bottomSheet()
                        ) {
                            SelectWalletScreen()
                        }
                        entry<UpsertWalletRoute>(
                            metadata = BottomSheetSceneStrategy.bottomSheet()
                        ) {
                            UpsertWalletScreen(walletId = it.walletId)
                        }
                        entry<WalletCreateFirstRoute> { CreateFirstWalletScreen() }

                        // category
                        entry<CategoryRoute> { ListCategoryScreen() }
                        entry<CategorySelectRoute>(
                            metadata = BottomSheetSceneStrategy.bottomSheet()
                        ) { navKey ->
                            SelectCategoryScreen(
                                selectedCategoryId = navKey.categoryId,
                                vm = hiltViewModel<SelectCategoryVM, SelectCategoryVM.Factory>(
                                    creationCallback = { it.create(navKey = navKey) }
                                )
                            )
                        }
                        entry<CategoryUpsertRoute>(
                            metadata = BottomSheetSceneStrategy.bottomSheet()
                        ) { navKey ->
                            UpsertCategoryScreen(
                                vm = hiltViewModel<UpsertCategoryVM, UpsertCategoryVM.Factory>(
                                    creationCallback = { it.create(navKey = navKey) }
                                )
                            )
                        }

                        // transaction
                        entry<TransactionUpsertRoute> { navKey ->
                            UpsertTransactionScreen(
                                vm = hiltViewModel<UpsertTransactionVM, UpsertTransactionVM.Factory>(
                                    creationCallback = { it.create(navKey = navKey) }
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
                        ) { TransactionActionSheet(transactionId = it.transactionId) }

                        // gold
                        entry<UpsertGoldRoute> { UpsertGoldScreen(goldId = it.goldId) }
                        entry<GoldTaxListRoute> { ListGoldTaxScreen() }
                        entry<UpsertGoldTaxRoute>(
                            metadata = BottomSheetSceneStrategy.bottomSheet()
                        ) { UpsertGoldTaxScreen(taxPreferenceId = it.id) }
                        entry<GoldDetailRoute> { GoldDetailScreen(goldId = it.id) }
                        // --> action sheet and dialog
                        entry<Routes.GoldActionSheetRoute>(
                            metadata = BottomSheetSceneStrategy.bottomSheet()
                        ) {
                            GoldActionSheet(
                                goldId = it.goldId
                            )
                        }
                        entry<Routes.DeleteGoldRoute>(
                            metadata = DialogSceneStrategy.dialog()
                        ) { navKey ->
                            DeleteGoldDialog(
                                vm = hiltViewModel<DeleteGoldVM, DeleteGoldVM.Factory>(
                                    creationCallback = { it.create(navKey = navKey) }
                                )
                            )
                        }

                        // home
                        entry<HomeGoldRoute> { HomeGoldScreen() }
                        entry<HomeRoute> { HomeScreen() }
                        entry<HomeTransactionRoute> { HomeTransactionScreen() }
                        entry<HomeSettingRoute> { HomeSettingScreen() }
                        entry<UpdateRoute> { CheckUpdateScreen() }
                    },
                )
            }
        }
    }
}
