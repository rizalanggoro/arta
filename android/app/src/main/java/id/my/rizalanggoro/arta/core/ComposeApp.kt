package id.my.rizalanggoro.arta.core

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import id.my.rizalanggoro.arta.core.Routes.AuthRoute
import id.my.rizalanggoro.arta.core.Routes.ForgotPasswordRoute
import id.my.rizalanggoro.arta.core.Routes.LoginRoute
import id.my.rizalanggoro.arta.core.Routes.RegisterRoute
import id.my.rizalanggoro.arta.feature.auth.presentation.forgotpassword.ForgotPasswordScreen
import id.my.rizalanggoro.arta.feature.auth.presentation.login.LoginScreen
import id.my.rizalanggoro.arta.feature.auth.presentation.register.RegisterScreen
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme

@Composable
fun ComposeApp() {
	ArtaTheme {
		val backStack = rememberNavBackStack(AuthRoute)
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
						entry<AuthRoute> { AuthGraph() }
					},
			)
		}
	}
}

@Composable
private fun AuthGraph() {
	val authBackStack = rememberNavBackStack(LoginRoute)
	CompositionLocalProvider(LocalBackStack provides authBackStack) {
		BackHandler(enabled = authBackStack.size > 1) {
			authBackStack.removeLastOrNull()
		}

		NavDisplay(
			backStack = authBackStack,
			onBack = {
				if (authBackStack.size > 1) {
					authBackStack.removeLastOrNull()
				}
			},
			entryDecorators =
				listOf(
					rememberSaveableStateHolderNavEntryDecorator(),
					rememberViewModelStoreNavEntryDecorator(),
				),
			entryProvider =
				entryProvider {
					entry<LoginRoute> { LoginScreen() }
					entry<RegisterRoute> { RegisterScreen() }
					entry<ForgotPasswordRoute> { ForgotPasswordScreen() }
				},
		)
	}
}
