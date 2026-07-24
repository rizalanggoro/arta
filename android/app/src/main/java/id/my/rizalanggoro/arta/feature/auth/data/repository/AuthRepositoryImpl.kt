package id.my.rizalanggoro.arta.feature.auth.data.repository

import id.my.rizalanggoro.arta.core.data.AuthPrefs
import id.my.rizalanggoro.arta.feature.auth.data.datasource.RemoteAuthDataSource
import id.my.rizalanggoro.arta.core.extension.authorization
import id.my.rizalanggoro.arta.feature.auth.domain.AuthSession
import id.my.rizalanggoro.arta.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val remoteAuthDataSource: RemoteAuthDataSource,
    private val authPrefs: AuthPrefs,
) : AuthRepository {

    override suspend fun login(email: String, password: String): AuthSession {
        val loginRes = remoteAuthDataSource.login(email, password)
        val session = AuthSession(
            userId = loginRes.userId,
            email = loginRes.email,
            name = loginRes.name,
            token = loginRes.token,
        )
        authPrefs.setSession(session)
        return session
    }

    override suspend fun register(name: String, email: String, password: String): AuthSession {
        val registerRes = remoteAuthDataSource.register(name, email, password)
        val session = AuthSession(
            userId = registerRes.userId,
            email = registerRes.email,
            name = registerRes.name,
            token = registerRes.token,
        )
        authPrefs.setSession(session)
        return session
    }

    override suspend fun logout() {
        remoteAuthDataSource.logout(authorization = authPrefs.authorization())
        authPrefs.clear()
    }
}
