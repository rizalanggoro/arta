package id.my.rizalanggoro.arta.core.application.route

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
object AuthRoute {
    @Serializable
    data object Login : NavKey

    @Serializable
    data object Register : NavKey

    @Serializable
    data object ForgotPassword : NavKey

    @Serializable
    data object Logout : NavKey
}