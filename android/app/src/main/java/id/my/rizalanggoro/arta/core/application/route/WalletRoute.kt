package id.my.rizalanggoro.arta.core.application.route

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
object WalletRoute {
    @Serializable
    data object List : NavKey

    @Serializable
    data object Select : NavKey

    @Serializable
    data object CreateFirst : NavKey

    @Serializable
    data class Upsert(
        val walletId: Int = 0,
    ) : NavKey

    @Serializable
    data class Delete(
        val walletId: Int,
    ) : NavKey
}