package id.my.rizalanggoro.arta.core.application.route

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
object OtherRoute {
    @Serializable
    data object CheckUpdate : NavKey
}