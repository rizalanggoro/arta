package id.my.rizalanggoro.arta.feature.auth.data

import id.my.rizalanggoro.arta.domain.AuthSession
import id.my.rizalanggoro.arta.core.dto.ApiErrorDto
import id.my.rizalanggoro.arta.feature.auth.data.dto.LoginRequestDto
import id.my.rizalanggoro.arta.feature.auth.data.dto.RegisterRequestDto
import id.my.rizalanggoro.arta.feature.auth.data.mapper.toDomain
import kotlinx.serialization.json.Json
import retrofit2.Response
import java.io.IOException

class AuthRepository(
    private val apiService: AuthApiService,
) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun login(email: String, password: String): Result<AuthSession> {
        return apiService.login(
            LoginRequestDto(
                email = email,
                password = password,
            ),
        ).toDomainResult()
    }

    suspend fun register(name: String, email: String, password: String, currency: String = "IDR"): Result<AuthSession> {
        return apiService.register(
            RegisterRequestDto(
                name = name,
                email = email,
                password = password,
                currency = currency,
            ),
        ).toDomainResult()
    }

    private fun Response<id.my.rizalanggoro.arta.feature.auth.data.dto.AuthResponseDto>.toDomainResult(): Result<AuthSession> {
        if (!isSuccessful) {
            return Result.failure(apiError(message = errorMessage()))
        }

        val body = body() ?: return Result.failure(apiError("Respons server kosong"))
        return Result.success(body.toDomain())
    }

    private fun Response<id.my.rizalanggoro.arta.feature.auth.data.dto.AuthResponseDto>.errorMessage(): String {
        val errorBody = errorBody()?.string().orEmpty()
        val message = runCatching {
            json.decodeFromString(ApiErrorDto.serializer(), errorBody).message
        }.getOrNull()

        return when {
            !message.isNullOrBlank() -> message
            code() in 400..499 -> "Permintaan tidak valid"
            code() >= 500 -> "Terjadi kesalahan pada server"
            else -> "Terjadi kesalahan tidak diketahui"
        }
    }

    private fun apiError(message: String): Throwable {
        return IllegalStateException(message)
    }

}