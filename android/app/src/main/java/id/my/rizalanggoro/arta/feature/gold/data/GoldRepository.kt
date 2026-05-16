package id.my.rizalanggoro.arta.feature.gold.data

import id.my.rizalanggoro.arta.core.dto.ApiErrorDto
import id.my.rizalanggoro.arta.domain.AuthSession
import id.my.rizalanggoro.arta.domain.Gold
import id.my.rizalanggoro.arta.feature.gold.data.dto.CreateGoldRequestDto
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
		pricePerGram: Double,
		type: String,
		purityPercent: Double,
		notes: String,
	): Result<Gold> {
		val authorization = authorizationHeader()
			?: return Result.failure(apiError("Sesi login tidak ditemukan"))

		return apiService.create(
			authorization = authorization,
			request = CreateGoldRequestDto(
				walletId = walletId,
				date = date,
				grams = grams,
				pricePerGram = pricePerGram,
				type = type,
				purityPercent = purityPercent,
				notes = notes,
			),
		).toDomainResult()
	}

	private fun authorizationHeader(): String? {
		val session = authSessionProvider()
		return session?.let { "${it.tokenType} ${it.token}" }
	}

	private fun Response<GoldResponseDto>.toDomainResult(): Result<Gold> {
		if (!isSuccessful) {
			return Result.failure(apiError(message = errorMessage()))
		}

		val body = body() ?: return Result.failure(apiError("Respons server kosong"))
		return Result.success(body.data.toDomain())
	}

	suspend fun getGoldById(id: Int): Result<Gold> {
		val authorization = authorizationHeader()
			?: return Result.failure(apiError("Sesi login tidak ditemukan"))

		return apiService.get(authorization = authorization, id = id).toDomainResult()
	}

	suspend fun updateGold(
		id: Int,
		date: String? = null,
		grams: Double? = null,
		pricePerGram: Double? = null,
		type: String? = null,
		purityPercent: Double? = null,
		notes: String? = null,
	): Result<Gold> {
		val authorization = authorizationHeader()
			?: return Result.failure(apiError("Sesi login tidak ditemukan"))

		return apiService.update(
			authorization = authorization,
			id = id,
			request = UpdateGoldRequestDto(
				date = date,
				grams = grams,
				pricePerGram = pricePerGram,
				type = type,
				purityPercent = purityPercent,
				notes = notes,
			),
		).toDomainResult()
	}

	suspend fun deleteGold(id: Int): Result<Unit> {
		val authorization = authorizationHeader()
			?: return Result.failure(apiError("Sesi login tidak ditemukan"))

		val response = apiService.delete(authorization = authorization, id = id)
		if (!response.isSuccessful) {
			return Result.failure(apiError(message = response.errorMessage()))
		}
		return Result.success(Unit)
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
