package id.my.rizalanggoro.arta.feature.wallet

import id.my.rizalanggoro.arta.core.extension.errorMessage
import kotlinx.serialization.json.Json
import retrofit2.Response

private val walletJson = Json {
    ignoreUnknownKeys = true
}

fun Response<*>.walletApiErrorMessage(): String {
    return errorMessage(walletJson)
}