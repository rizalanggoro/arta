package id.my.rizalanggoro.arta.core.application.entry

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import id.my.rizalanggoro.arta.core.application.route.AuthRoute
import id.my.rizalanggoro.arta.feature.auth.presentation.forgotpassword.ForgotPasswordScreen
import id.my.rizalanggoro.arta.feature.auth.presentation.login.LoginScreen
import id.my.rizalanggoro.arta.feature.auth.presentation.logout.LogoutDialog
import id.my.rizalanggoro.arta.feature.auth.presentation.register.RegisterScreen

fun EntryProviderScope<NavKey>.authEntry() {
    entry<AuthRoute.Login> {
        LoginScreen()
    }

    entry<AuthRoute.Register> {
        RegisterScreen()
    }

    entry<AuthRoute.ForgotPassword> {
        ForgotPasswordScreen()
    }

    entry<AuthRoute.Logout>(
        metadata = DialogSceneStrategy.dialog()
    ) {
        LogoutDialog()
    }
}