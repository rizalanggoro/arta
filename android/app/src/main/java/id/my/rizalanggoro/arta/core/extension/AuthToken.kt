package id.my.rizalanggoro.arta.core.extension

import id.my.rizalanggoro.arta.core.data.AuthPrefs

fun AuthPrefs.authorization(): String {
    val token = this.currentSession.value?.token
        ?: throw IllegalStateException("Sesi login tidak ditemukan")
    return "Bearer $token"
}