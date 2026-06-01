package id.my.rizalanggoro.arta.core.application.entry

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import id.my.rizalanggoro.arta.core.application.Routes
import id.my.rizalanggoro.arta.core.application.Routes.ForgotPasswordRoute
import id.my.rizalanggoro.arta.core.application.Routes.LoginRoute
import id.my.rizalanggoro.arta.core.application.Routes.RegisterRoute
import id.my.rizalanggoro.arta.feature.auth.presentation.forgotpassword.ForgotPasswordScreen
import id.my.rizalanggoro.arta.feature.auth.presentation.login.LoginScreen
import id.my.rizalanggoro.arta.feature.auth.presentation.logout.LogoutDialog
import id.my.rizalanggoro.arta.feature.auth.presentation.register.RegisterScreen

fun EntryProviderScope<NavKey>.authEntry() {
    entry<LoginRoute> {
        LoginScreen()
    }

    entry<RegisterRoute> {
        RegisterScreen()
    }

    entry<ForgotPasswordRoute> {
        ForgotPasswordScreen()
    }

    entry<Routes.LogoutRoute>(
        metadata = DialogSceneStrategy.dialog()
    ) {
        LogoutDialog()
    }
}