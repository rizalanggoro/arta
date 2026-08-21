package id.my.rizalanggoro.arta.core.application.route

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
object GoldRoute {
    @Serializable
    enum class PriceHistoryType {
        GOLD, FX;

        val queryValue: String
            get() = when (this) {
                GOLD -> "gold"
                FX -> "fx"
            }
    }

    @Serializable
    data class PriceHistory(
        val type: PriceHistoryType,
    ) : NavKey

    @Serializable
    data object ListTax : NavKey

    @Serializable
    data class Detail(
        val id: Int,
    ) : NavKey

    @Serializable
    data class Upsert(
        val goldId: Int = 0,
    ) : NavKey

    @Serializable
    data class UpsertTax(
        val id: Int = 0,
    ) : NavKey

    @Serializable
    data class Delete(
        val goldId: Int,
    ) : NavKey

    @Serializable
    data class ActionSheet(
        val goldId: Int,
    ) : NavKey
}