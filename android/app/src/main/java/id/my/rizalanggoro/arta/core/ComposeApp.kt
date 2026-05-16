package id.my.rizalanggoro.arta.core

import androidx.activity.compose.BackHandler
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
import id.my.rizalanggoro.arta.core.Routes.AuthRoute
import id.my.rizalanggoro.arta.core.Routes.CategoryCreateRoute
import id.my.rizalanggoro.arta.core.Routes.CategoryRoute
import id.my.rizalanggoro.arta.core.Routes.CategorySelectRoute
import id.my.rizalanggoro.arta.core.Routes.CategoryUpdateRoute
import id.my.rizalanggoro.arta.core.Routes.ForgotPasswordRoute
import id.my.rizalanggoro.arta.core.Routes.LoginRoute
import id.my.rizalanggoro.arta.core.Routes.RegisterRoute
import id.my.rizalanggoro.arta.core.Routes.GoldFormRoute
import id.my.rizalanggoro.arta.core.Routes.GoldDetailRoute
import id.my.rizalanggoro.arta.core.Routes.GoldRoute
import id.my.rizalanggoro.arta.core.Routes.DashboardRoute
import id.my.rizalanggoro.arta.core.Routes.TransactionListRoute
import id.my.rizalanggoro.arta.core.Routes.SettingsRoute
import id.my.rizalanggoro.arta.core.Routes.WalletRoute
import id.my.rizalanggoro.arta.core.Routes.WalletSelectRoute
import id.my.rizalanggoro.arta.core.Routes.WalletUpdateRoute
import id.my.rizalanggoro.arta.feature.auth.presentation.forgotpassword.ForgotPasswordScreen
import id.my.rizalanggoro.arta.feature.auth.presentation.login.LoginScreen
import id.my.rizalanggoro.arta.feature.auth.presentation.register.RegisterScreen
import id.my.rizalanggoro.arta.core.Routes.TransactionFormRoute
import id.my.rizalanggoro.arta.feature.transaction.presentation.create.CreateTransactionScreen
import id.my.rizalanggoro.arta.core.Routes.TransactionDetailRoute
import id.my.rizalanggoro.arta.feature.transaction.presentation.update.UpdateTransactionScreen
import id.my.rizalanggoro.arta.feature.transaction.presentation.detail.TransactionDetailScreen
import id.my.rizalanggoro.arta.feature.category.presentation.create.CreateCategoryScreen
import id.my.rizalanggoro.arta.feature.category.presentation.list.ListCategoryScreen
import id.my.rizalanggoro.arta.feature.category.presentation.select.SelectCategoryScreen
import id.my.rizalanggoro.arta.feature.category.presentation.update.UpdateCategoryScreen
import id.my.rizalanggoro.arta.feature.gold.presentation.create.CreateGoldScreen
import id.my.rizalanggoro.arta.feature.gold.presentation.detail.GoldDetailScreen
import id.my.rizalanggoro.arta.feature.gold.presentation.update.UpdateGoldScreen
import id.my.rizalanggoro.arta.feature.wallet.presentation.list.ListWalletScreen
import id.my.rizalanggoro.arta.feature.wallet.presentation.select.SelectWalletScreen
import id.my.rizalanggoro.arta.feature.wallet.presentation.update.UpdateWalletScreen
import id.my.rizalanggoro.arta.feature.home.presentation.home.HomeScreen
import id.my.rizalanggoro.arta.feature.home.presentation.gold.HomeGoldListContent
import id.my.rizalanggoro.arta.feature.home.presentation.transaction.HomeTransactionListContent
import id.my.rizalanggoro.arta.feature.home.presentation.setting.HomeSettingContent
import id.my.rizalanggoro.arta.feature.home.presentation.home.HomeWalletType
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme

@Composable
fun ComposeApp() {
	ArtaTheme {
		val app = LocalContext.current.applicationContext as id.my.rizalanggoro.arta.core.application.MyApplication
		val session by app.authPrefs.currentSession.collectAsState()

		val startRoute = if (session != null) CategoryRoute else AuthRoute

		val backStack = rememberNavBackStack(startRoute)
		CompositionLocalProvider(LocalBackStack provides backStack) {
			NavDisplay(
				backStack = backStack,
				onBack = {
					if (backStack.size > 1) {
						backStack.removeLastOrNull()
					}
				},
				entryDecorators =
					listOf(
						rememberSaveableStateHolderNavEntryDecorator(),
						rememberViewModelStoreNavEntryDecorator(),
					),
				entryProvider =
					entryProvider {
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
						entry<TransactionDetailRoute> { route -> TransactionDetailScreen(transactionId = route.id) }
						entry<GoldDetailRoute> { route -> GoldDetailScreen(goldId = route.id) }
						entry<GoldRoute> { HomeGoldListContent() }
						entry<DashboardRoute> { HomeScreen() }
						entry<TransactionListRoute> { HomeTransactionListContent() }
						entry<SettingsRoute> { HomeSettingContent(walletType = HomeWalletType.CashSavings) }
						entry<WalletRoute> { ListWalletScreen() }
						entry<WalletSelectRoute> { SelectWalletScreen() }
						entry<WalletUpdateRoute> { route -> UpdateWalletScreen(walletId = route.walletId) }
						entry<LoginRoute> { LoginScreen() }
						entry<RegisterRoute> { RegisterScreen() }
						entry<ForgotPasswordRoute> { ForgotPasswordScreen() }
					},
			)
		}
	}
}
