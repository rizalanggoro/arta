package id.my.rizalanggoro.arta.core

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import id.my.rizalanggoro.arta.core.Routes.CategoryCreateRoute
import id.my.rizalanggoro.arta.core.Routes.CategoryRoute
import id.my.rizalanggoro.arta.core.Routes.CategorySelectRoute
import id.my.rizalanggoro.arta.core.Routes.CategoryUpdateRoute
import id.my.rizalanggoro.arta.core.Routes.ForgotPasswordRoute
import id.my.rizalanggoro.arta.core.Routes.GoldCreateRoute
import id.my.rizalanggoro.arta.core.Routes.GoldDetailRoute
import id.my.rizalanggoro.arta.core.Routes.GoldUpdateRoute
import id.my.rizalanggoro.arta.core.Routes.HomeGoldRoute
import id.my.rizalanggoro.arta.core.Routes.HomeRoute
import id.my.rizalanggoro.arta.core.Routes.HomeSettingRoute
import id.my.rizalanggoro.arta.core.Routes.HomeTransactionRoute
import id.my.rizalanggoro.arta.core.Routes.LoginRoute
import id.my.rizalanggoro.arta.core.Routes.RegisterRoute
import id.my.rizalanggoro.arta.core.Routes.TransactionCreateRoute
import id.my.rizalanggoro.arta.core.Routes.TransactionDetailRoute
import id.my.rizalanggoro.arta.core.Routes.TransactionUpdateRoute
import id.my.rizalanggoro.arta.core.Routes.WalletCreateFirstRoute
import id.my.rizalanggoro.arta.core.Routes.WalletCreateRoute
import id.my.rizalanggoro.arta.core.Routes.WalletRoute
import id.my.rizalanggoro.arta.core.Routes.WalletSelectRoute
import id.my.rizalanggoro.arta.core.Routes.WalletUpdateRoute
import id.my.rizalanggoro.arta.core.application.MyApplication
import id.my.rizalanggoro.arta.feature.auth.presentation.forgotpassword.ForgotPasswordScreen
import id.my.rizalanggoro.arta.feature.auth.presentation.login.LoginScreen
import id.my.rizalanggoro.arta.feature.auth.presentation.register.RegisterScreen
import id.my.rizalanggoro.arta.feature.category.presentation.create.CreateCategoryScreen
import id.my.rizalanggoro.arta.feature.category.presentation.list.ListCategoryScreen
import id.my.rizalanggoro.arta.feature.category.presentation.select.SelectCategoryScreen
import id.my.rizalanggoro.arta.feature.category.presentation.update.UpdateCategoryScreen
import id.my.rizalanggoro.arta.feature.gold.presentation.create.CreateGoldScreen
import id.my.rizalanggoro.arta.feature.gold.presentation.detail.GoldDetailScreen
import id.my.rizalanggoro.arta.feature.gold.presentation.update.UpdateGoldScreen
import id.my.rizalanggoro.arta.feature.home.presentation.gold.HomeGoldScreen
import id.my.rizalanggoro.arta.feature.home.presentation.home.HomeScreen
import id.my.rizalanggoro.arta.feature.home.presentation.setting.HomeSettingScreen
import id.my.rizalanggoro.arta.feature.home.presentation.transaction.HomeTransactionScreen
import id.my.rizalanggoro.arta.feature.transaction.presentation.create.CreateTransactionScreen
import id.my.rizalanggoro.arta.feature.transaction.presentation.detail.TransactionDetailScreen
import id.my.rizalanggoro.arta.feature.transaction.presentation.update.UpdateTransactionScreen
import id.my.rizalanggoro.arta.feature.wallet.presentation.create.CreateWalletScreen
import id.my.rizalanggoro.arta.feature.wallet.presentation.createfirst.CreateFirstWalletScreen
import id.my.rizalanggoro.arta.feature.wallet.presentation.list.ListWalletScreen
import id.my.rizalanggoro.arta.feature.wallet.presentation.select.SelectWalletScreen
import id.my.rizalanggoro.arta.feature.wallet.presentation.update.UpdateWalletScreen
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme

@Composable
fun ComposeApp() {
    val app = LocalContext.current.applicationContext as MyApplication
    val session by app.authPrefs.currentSession.collectAsState()
    val selectedWallet by app.selectedWalletPrefs.selectedWallet.collectAsState()
    val isDarkTheme by app.themePrefs.isDarkTheme.collectAsState()

    ArtaTheme(darkTheme = isDarkTheme) {

        val startRoute = when {
            session != null && selectedWallet == null -> WalletCreateFirstRoute
            session != null -> HomeRoute
            else -> LoginRoute
        }

        val backStack = rememberNavBackStack(startRoute)

        CompositionLocalProvider(LocalBackStack provides backStack) {
            Surface {
                NavDisplay(
                    backStack = backStack,
                    entryDecorators = listOf(
                        rememberSaveableStateHolderNavEntryDecorator(),
                        rememberViewModelStoreNavEntryDecorator(),
                    ),
                    entryProvider = entryProvider {
                        entry<CategoryRoute> { ListCategoryScreen() }
                        entry<CategorySelectRoute> { SelectCategoryScreen() }
                        entry<CategoryCreateRoute> { CreateCategoryScreen() }
                        entry<CategoryUpdateRoute> { route -> UpdateCategoryScreen(categoryId = route.categoryId) }
                        entry<GoldCreateRoute> { CreateGoldScreen() }
                        entry<GoldUpdateRoute> { route -> UpdateGoldScreen(goldId = route.goldId) }
                        entry<TransactionCreateRoute> { CreateTransactionScreen() }
                        entry<TransactionUpdateRoute> { route ->
                            UpdateTransactionScreen(
                                transactionId = route.transactionId
                            )
                        }
                        entry<TransactionDetailRoute> { route ->
                            TransactionDetailScreen(
                                transactionId = route.id
                            )
                        }
                        entry<GoldDetailRoute> { route -> GoldDetailScreen(goldId = route.id) }
                        entry<HomeGoldRoute> { HomeGoldScreen() }
                        entry<HomeRoute> {
                            HomeScreen()
                        }
                        entry<HomeTransactionRoute> { HomeTransactionScreen() }
                        entry<HomeSettingRoute> { HomeSettingScreen() }
                        entry<WalletRoute> { ListWalletScreen() }
                        entry<WalletSelectRoute> { SelectWalletScreen() }
                        entry<WalletCreateRoute> { CreateWalletScreen() }
                        entry<WalletCreateFirstRoute> { CreateFirstWalletScreen() }
                        entry<WalletUpdateRoute> { route -> UpdateWalletScreen(walletId = route.walletId) }
                        entry<LoginRoute> { LoginScreen() }
                        entry<RegisterRoute> { RegisterScreen() }
                        entry<ForgotPasswordRoute> { ForgotPasswordScreen() }
                    },
                )
            }
        }
    }
}
