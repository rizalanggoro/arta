package id.my.rizalanggoro.arta.core.extension

import id.my.rizalanggoro.arta.openapi.models.DtoError
import kotlinx.serialization.json.Json
import retrofit2.Response

private val json = Json {
    ignoreUnknownKeys = true
}

fun Response<*>.errorMessage(): String {
    val errorBody = errorBody()?.string().orEmpty()
    val message = runCatching {
        json.decodeFromString(DtoError.serializer(), errorBody).message
    }.getOrNull()

    return when {
        !message.isNullOrBlank() -> message
        code() in 400..499 -> "Permintaan tidak valid"
        code() >= 500 -> "Terjadi kesalahan pada server"
        else -> "Terjadi kesalahan tidak diketahui"
    }
}
