package id.my.rizalanggoro.arta.core.application.route

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
object HomeRoute {
    @Serializable
    data object Index : NavKey

    @Serializable
    data object CashDashboard : NavKey

    @Serializable
    data object GoldDashboard : NavKey

    @Serializable
    data object ListTransaction : NavKey

    @Serializable
    data object ListGold : NavKey

    @Serializable
    data object Setting : NavKey
}