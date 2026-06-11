package id.my.rizalanggoro.arta.core.application.route

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
object CategoryRoute {
    @Serializable
    data object List : NavKey

    @Serializable
    data class Select(
        val categoryId: Int? = null,
    ) : NavKey

    @Serializable
    data class Upsert(
        val categoryId: Int = 0,
    ) : NavKey
}