package id.my.rizalanggoro.arta.feature.gold.data

import id.my.rizalanggoro.arta.core.extension.errorMessage
import id.my.rizalanggoro.arta.domain.AuthSession
import id.my.rizalanggoro.arta.domain.Gold
import id.my.rizalanggoro.arta.feature.gold.data.dto.CreateGoldRequestDto
import id.my.rizalanggoro.arta.feature.gold.data.dto.GoldListResponseDto
import id.my.rizalanggoro.arta.feature.gold.data.dto.GoldResponseDto
import id.my.rizalanggoro.arta.feature.gold.data.dto.UpdateGoldRequestDto
import id.my.rizalanggoro.arta.feature.gold.data.mapper.toDomain
import kotlinx.serialization.json.Json
import retrofit2.Response

class GoldRepository(
    private val apiService: GoldApiService,
    private val authSessionProvider: () -> AuthSession?,
) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun createGold(
        walletId: Int,
        date: String,
        grams: Double,
        price: Double,
        type: String,
        carat: Double,
        notes: String,
    ): Result<Gold> {
        return runCatching {
            val authorization = authorizationHeader()
                ?: throw apiError("Sesi login tidak ditemukan")

            apiService.create(
                authorization = authorization,
                request = CreateGoldRequestDto(
                    walletId = walletId,
                    date = date,
                    grams = grams,
                    price = price,
                    type = type,
                    carat = carat,
                    notes = notes,
                ),
            ).toDomainResult()
        }.fold(
            onSuccess = { it },
            onFailure = { Result.failure(it) },
        )
    }

    suspend fun getGoldById(id: Int): Result<Gold> {
        return runCatching {
            val authorization = authorizationHeader()
                ?: throw apiError("Sesi login tidak ditemukan")

            apiService.get(authorization = authorization, id = id).toDomainResult()
        }.fold(
            onSuccess = { it },
            onFailure = { Result.failure(it) },
        )
    }

    suspend fun updateGold(
        id: Int,
        date: String? = null,
        grams: Double? = null,
        price: Double? = null,
        type: String? = null,
        carat: Double? = null,
        notes: String? = null,
    ): Result<Gold> {
        return runCatching {
            val authorization = authorizationHeader()
                ?: throw apiError("Sesi login tidak ditemukan")

            apiService.update(
                authorization = authorization,
                id = id,
                request = UpdateGoldRequestDto(
                    date = date,
                    grams = grams,
                    price = price,
                    type = type,
                    carat = carat,
                    notes = notes,
                ),
            ).toDomainResult()
        }.fold(
            onSuccess = { it },
            onFailure = { Result.failure(it) },
        )
    }

    suspend fun listGolds(): Result<List<Gold>> {
        return runCatching {
            val authorization = authorizationHeader()
                ?: throw apiError("Sesi login tidak ditemukan")

            val response = apiService.list(authorization = authorization)
            if (!response.isSuccessful) {
                return@runCatching Result.failure(apiError(message = response.errorMessage(json)))
            }

            val body = response.body() ?: return@runCatching Result.failure(apiError("Respons server kosong"))
            Result.success(body.golds.map { it.data.toDomain() })
        }.fold(
            onSuccess = { it },
            onFailure = { Result.failure(it) },
        )
    }

    suspend fun deleteGold(id: Int): Result<Unit> {
        return runCatching {
            val authorization = authorizationHeader()
                ?: throw apiError("Sesi login tidak ditemukan")

            val response = apiService.delete(authorization = authorization, id = id)
            if (!response.isSuccessful) {
                return@runCatching Result.failure(apiError(message = response.errorMessage(json)))
            }

            Result.success(Unit)
        }.fold(
            onSuccess = { it },
            onFailure = { Result.failure(it) },
        )
    }

    private fun authorizationHeader(): String? {
        val session = authSessionProvider()
        return session?.let { "Bearer ${it.token}" }
    }

    private fun Response<GoldResponseDto>.toDomainResult(): Result<Gold> {
        if (!isSuccessful) {
            return Result.failure(apiError(message = errorMessage(json)))
        }

        val body = body() ?: return Result.failure(apiError("Respons server kosong"))
        return Result.success(body.data.toDomain())
    }

    private fun apiError(message: String): Throwable {
        return IllegalStateException(message)
    }
}
