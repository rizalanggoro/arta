package id.my.rizalanggoro.arta.feature.auth.domain.repository

import id.my.rizalanggoro.arta.feature.auth.domain.AuthSession

interface AuthRepository {
    suspend fun login(email: String, password: String): AuthSession
    suspend fun register(name: String, email: String, password: String): AuthSession
    suspend fun logout()
}
