package id.my.rizalanggoro.arta.feature.auth.data.datasource

import id.my.rizalanggoro.arta.core.extension.errorMessage
import id.my.rizalanggoro.arta.openapi.apis.AuthApi
import id.my.rizalanggoro.arta.openapi.models.LoginReq
import id.my.rizalanggoro.arta.openapi.models.LoginRes
import id.my.rizalanggoro.arta.openapi.models.LogoutRes
import id.my.rizalanggoro.arta.openapi.models.RegisterReq
import id.my.rizalanggoro.arta.openapi.models.RegisterRes
import javax.inject.Inject

class RemoteAuthDataSource @Inject constructor(
    private val authApi: AuthApi,
) {
    suspend fun login(email: String, password: String): LoginRes {
        val response = authApi.apiAuthLoginPost(
            LoginReq(email = email, password = password)
        )
        if (!response.isSuccessful) {
            throw IllegalStateException(response.errorMessage())
        }
        return response.body() ?: throw IllegalStateException("Respons server kosong")
    }

    suspend fun register(name: String, email: String, password: String): RegisterRes {
        val response = authApi.apiAuthRegisterPost(
            RegisterReq(name = name, email = email, password = password)
        )
        if (!response.isSuccessful) {
            throw IllegalStateException("Registrasi gagal")
        }
        return response.body() ?: throw IllegalStateException("Respons server kosong")
    }

    suspend fun logout(authorization: String): LogoutRes {
        val response = authApi.logout(authorization = authorization)
        if (!response.isSuccessful) {
            throw IllegalStateException(response.errorMessage())
        }
        return response.body() ?: throw IllegalStateException("Respons server kosong")
    }
}
