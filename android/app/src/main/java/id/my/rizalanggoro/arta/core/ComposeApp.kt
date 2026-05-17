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
import id.my.rizalanggoro.arta.core.Routes.GoldDetailRoute
import id.my.rizalanggoro.arta.core.Routes.GoldFormRoute
import id.my.rizalanggoro.arta.core.Routes.GoldRoute
import id.my.rizalanggoro.arta.core.Routes.HomeRoute
import id.my.rizalanggoro.arta.core.Routes.LoginRoute
import id.my.rizalanggoro.arta.core.Routes.RegisterRoute
import id.my.rizalanggoro.arta.core.Routes.SettingsRoute
import id.my.rizalanggoro.arta.core.Routes.TransactionDetailRoute
import id.my.rizalanggoro.arta.core.Routes.TransactionFormRoute
import id.my.rizalanggoro.arta.core.Routes.TransactionListRoute
import id.my.rizalanggoro.arta.core.Routes.WalletRoute
import id.my.rizalanggoro.arta.core.Routes.WalletSelectRoute
import id.my.rizalanggoro.arta.core.Routes.WalletUpdateRoute
import id.my.rizalanggoro.arta.core.Routes.WalletCreateRoute
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
import id.my.rizalanggoro.arta.feature.home.presentation.dashboard.cash.HomeCashDashboardScreen
import id.my.rizalanggoro.arta.feature.home.presentation.gold.HomeGoldListContent
import id.my.rizalanggoro.arta.feature.home.presentation.home.HomeScreen
import id.my.rizalanggoro.arta.feature.home.presentation.home.HomeWalletType
import id.my.rizalanggoro.arta.feature.home.presentation.setting.HomeSettingScreen
import id.my.rizalanggoro.arta.feature.home.presentation.transaction.HomeTransactionListScreen
import id.my.rizalanggoro.arta.feature.transaction.presentation.create.CreateTransactionScreen
import id.my.rizalanggoro.arta.feature.transaction.presentation.detail.TransactionDetailScreen
import id.my.rizalanggoro.arta.feature.transaction.presentation.update.UpdateTransactionScreen
import id.my.rizalanggoro.arta.feature.wallet.presentation.list.ListWalletScreen
import id.my.rizalanggoro.arta.feature.wallet.presentation.select.SelectWalletScreen
import id.my.rizalanggoro.arta.feature.wallet.presentation.create.CreateWalletScreen
import id.my.rizalanggoro.arta.feature.wallet.presentation.update.UpdateWalletScreen
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme

@Composable
fun ComposeApp() {
    val app = LocalContext.current.applicationContext as MyApplication
    val session by app.authPrefs.currentSession.collectAsState()
    val isDarkTheme by app.themePrefs.isDarkTheme.collectAsState()

    ArtaTheme(darkTheme = isDarkTheme) {

        val startRoute = when {
            session != null -> CategoryRoute
            else -> LoginRoute
        }

        val backStack = rememberNavBackStack(Routes.HomeRoute)

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
                        entry<GoldFormRoute> { route ->
                            if (route.goldId != null) {
                                UpdateGoldScreen(goldId = route.goldId)
                            } else {
                                CreateGoldScreen(walletId = route.walletId)
                            }
                        }
                        entry<TransactionFormRoute> { route ->
                            if (route.transactionId != null) {
                                UpdateTransactionScreen(transactionId = route.transactionId)
                            } else {
                                CreateTransactionScreen(walletId = route.walletId)
                            }
                        }
                        entry<TransactionDetailRoute> { route ->
                            TransactionDetailScreen(
                                transactionId = route.id
                            )
                        }
                        entry<GoldDetailRoute> { route -> GoldDetailScreen(goldId = route.id) }
                        entry<GoldRoute> { HomeGoldListContent() }
                        entry<HomeRoute> {
                            HomeScreen()
                        }
                        entry<TransactionListRoute> { HomeTransactionListScreen() }
                        entry<SettingsRoute> { HomeSettingScreen() }
                        entry<WalletRoute> { ListWalletScreen() }
                        entry<WalletSelectRoute> { SelectWalletScreen() }
                        entry<WalletCreateRoute> { CreateWalletScreen() }
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
