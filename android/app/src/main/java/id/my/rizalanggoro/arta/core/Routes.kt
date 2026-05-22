package id.my.rizalanggoro.arta.core

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
object Routes {
    // auth
    @Serializable
    data object LoginRoute : NavKey

    @Serializable
    data object GoldTaxListRoute : NavKey

    data object RegisterRoute : NavKey

    @Serializable
    data object ForgotPasswordRoute : NavKey

    // home
    @Serializable
    data object HomeRoute : NavKey

    @Serializable
    data object HomeCashDashboardRoute : NavKey

    @Serializable
    data object HomeGoldDashboardRoute : NavKey

    @Serializable
    data object HomeTransactionRoute : NavKey

    @Serializable
    data object HomeGoldRoute : NavKey

    @Serializable
    data object HomeSettingRoute : NavKey

    // wallet
    @Serializable
    data object WalletRoute : NavKey

    @Serializable
    data object WalletSelectRoute : NavKey

    @Serializable
    data class UpsertWalletRoute(
        val walletId: Int = 0,
    ) : NavKey

    @Serializable
    data object WalletCreateFirstRoute : NavKey

    // gold
    @Serializable
    data object GoldCreateRoute : NavKey

    @Serializable
    data class GoldUpdateRoute(
        val goldId: Int,
    ) : NavKey

    @Serializable
    data class GoldDetailRoute(
        val id: Int,
    ) : NavKey

    @Serializable
    data class UpsertGoldTaxRoute(
        val id: Int = 0
    ) : NavKey

    // category
    @Serializable
    data object CategoryRoute : NavKey

    @Serializable
    data object CategorySelectRoute : NavKey

    @Serializable
    data class CategoryUpsertRoute(
        val categoryId: Int = 0,
    ) : NavKey

    // transaction
    @Serializable
    data object TransactionCreateRoute : NavKey

    @Serializable
    data class TransactionUpdateRoute(
        val transactionId: Int,
    ) : NavKey

    @Serializable
    data class TransactionDetailRoute(
        val id: Int,
    ) : NavKey
}
