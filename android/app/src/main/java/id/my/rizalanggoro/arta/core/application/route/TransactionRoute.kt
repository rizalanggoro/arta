package id.my.rizalanggoro.arta.core.application.route

import androidx.navigation3.runtime.NavKey
import id.my.rizalanggoro.arta.core.constant.TransactionTimeRangeType
import kotlinx.serialization.Serializable

@Serializable
object TransactionRoute {
    @Serializable
    data class Upsert(
        val transactionId: Int = 0,
    ) : NavKey

    @Serializable
    data class Detail(
        val transactionId: Int,
    ) : NavKey

    @Serializable
    data class Delete(
        val transactionId: Int,
    ) : NavKey

    @Serializable
    data class ActionSheet(
        val transactionId: Int,
    ) : NavKey

    @Serializable
    data object Filter : NavKey

    @Serializable
    data class Chart(
        val type: String,
        val timeRange: TransactionTimeRangeType,
        val timeRangeOffset: Int = 0,
    ) : NavKey
}