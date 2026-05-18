package id.my.rizalanggoro.arta.feature.home.data

import id.my.rizalanggoro.arta.core.dto.ApiErrorDto
import id.my.rizalanggoro.arta.domain.AuthSession
import id.my.rizalanggoro.arta.domain.CashDashboardOverview
import id.my.rizalanggoro.arta.domain.GoldDashboardOverview
import id.my.rizalanggoro.arta.feature.home.data.dto.CashDashboardResponseDto
import id.my.rizalanggoro.arta.feature.home.data.dto.GoldDashboardResponseDto
import id.my.rizalanggoro.arta.feature.home.data.mapper.toDomain
import kotlinx.serialization.json.Json
import retrofit2.Response

class DashboardRepository(
    private val apiService: DashboardApiService,
    private val authSessionProvider: () -> AuthSession?,
) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun getCashDashboard(): Result<CashDashboardOverview> {
        return runCatching {
            val authorization = authorizationHeader()
                ?: throw apiError("Sesi login tidak ditemukan")

            apiService.getCashDashboard(authorization).toDomainResult()
        }.getOrElse { Result.failure(it) }
    }

    suspend fun getGoldDashboard(): Result<GoldDashboardOverview> {
        return runCatching {
            val authorization = authorizationHeader()
                ?: throw apiError("Sesi login tidak ditemukan")

            apiService.getGoldDashboard(authorization).toGoldDomainResult()
        }.getOrElse { Result.failure(it) }
    }

    private fun authorizationHeader(): String? {
        val session = authSessionProvider()
        return session?.let { "Bearer ${it.token}" }
    }

    private fun Response<CashDashboardResponseDto>.toDomainResult(): Result<CashDashboardOverview> {
        if (!isSuccessful) {
            return Result.failure(apiError(message = errorMessage()))
        }

        val body = body() ?: return Result.failure(apiError("Respons server kosong"))
        return Result.success(body.toDomain())
    }

    private fun Response<GoldDashboardResponseDto>.toGoldDomainResult(): Result<GoldDashboardOverview> {
        if (!isSuccessful) {
            return Result.failure(apiError(message = errorMessage()))
        }

        val body = body() ?: return Result.failure(apiError("Respons server kosong"))
        return Result.success(body.toDomain())
    }

    private fun Response<*>.errorMessage(): String {
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