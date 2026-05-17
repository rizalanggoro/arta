package id.my.rizalanggoro.arta.core

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
object Routes {
    @Serializable
    data object LoginRoute : NavKey

    @Serializable
    data object RegisterRoute : NavKey

    @Serializable
    data object ForgotPasswordRoute : NavKey

    @Serializable
    data object DashboardRoute : NavKey

    @Serializable
    data object TransactionListRoute : NavKey

    @Serializable
    data class TransactionFormRoute(
        val transactionId: Int? = null,
        val walletId: Int? = null,
    ) : NavKey

    @Serializable
    data class TransactionDetailRoute(
        val id: Int,
    ) : NavKey

    @Serializable
    data object GoldRoute : NavKey

    @Serializable
    data class GoldFormRoute(
        val goldId: Int? = null,
        val walletId: Int? = null,
    ) : NavKey

    @Serializable
    data object WalletRoute : NavKey

    @Serializable
    data object WalletSelectRoute : NavKey

    @Serializable
    data class WalletUpdateRoute(
        val walletId: Int,
    ) : NavKey

    @Serializable
    data class GoldDetailRoute(
        val id: Int,
    ) : NavKey

    @Serializable
    data object CategoryRoute : NavKey

    @Serializable
    data object CategorySelectRoute : NavKey

    @Serializable
    data object CategoryCreateRoute : NavKey

    @Serializable
    data class CategoryUpdateRoute(
        val categoryId: Int,
    ) : NavKey

    @Serializable
    data object ProfileRoute : NavKey

    @Serializable
    data object SettingsRoute : NavKey
}
