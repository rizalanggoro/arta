package id.my.rizalanggoro.arta.feature.wallet.data

import id.my.rizalanggoro.arta.core.dto.ApiErrorDto
import id.my.rizalanggoro.arta.domain.AuthSession
import id.my.rizalanggoro.arta.domain.Wallet
import id.my.rizalanggoro.arta.feature.wallet.data.dto.CreateWalletRequestDto
import id.my.rizalanggoro.arta.feature.wallet.data.dto.DeleteWalletResponseDto
import id.my.rizalanggoro.arta.feature.wallet.data.dto.UpdateWalletRequestDto
import id.my.rizalanggoro.arta.feature.wallet.data.dto.WalletListResponseDto
import id.my.rizalanggoro.arta.feature.wallet.data.dto.WalletResponseDto
import id.my.rizalanggoro.arta.feature.wallet.data.mapper.toDomain
import kotlinx.serialization.json.Json
import retrofit2.Response

class WalletRepository(
	private val apiService: WalletApiService,
	private val authSessionProvider: () -> AuthSession?,
) {
	private val json = Json { ignoreUnknownKeys = true }

	suspend fun getWallets(): Result<List<Wallet>> {
		val authorization = authorizationHeader()
			?: return Result.failure(apiError("Sesi login tidak ditemukan"))

		return apiService.list(authorization).toListResult()
	}

	suspend fun getWalletById(id: Int): Result<Wallet> {
		val authorization = authorizationHeader()
			?: return Result.failure(apiError("Sesi login tidak ditemukan"))

		return apiService.get(authorization, id).toDomainResult()
	}

	suspend fun updateWallet(
		id: Int,
		name: String,
		type: String,
		isDefault: Boolean,
	): Result<Wallet> {
		val authorization = authorizationHeader()
			?: return Result.failure(apiError("Sesi login tidak ditemukan"))

		return apiService.update(
			authorization = authorization,
			id = id,
			request = UpdateWalletRequestDto(
				name = name,
				type = type,
				isDefault = isDefault,
			),
		).toDomainResult()
	}

	suspend fun deleteWallet(id: Int): Result<Unit> {
		val authorization = authorizationHeader()
			?: return Result.failure(apiError("Sesi login tidak ditemukan"))

		return apiService.delete(authorization, id).toUnitResult()
	}

	private fun authorizationHeader(): String? {
		val session = authSessionProvider()
		return session?.let { "${it.tokenType} ${it.token}" }
	}

	private fun Response<WalletResponseDto>.toDomainResult(): Result<Wallet> {
		if (!isSuccessful) {
			return Result.failure(apiError(message = errorMessage()))
		}

		val body = body() ?: return Result.failure(apiError("Respons server kosong"))
		return Result.success(body.data.toDomain())
	}

	private fun Response<WalletListResponseDto>.toListResult(): Result<List<Wallet>> {
		if (!isSuccessful) {
			return Result.failure(apiError(message = errorMessage()))
		}

		val body = body() ?: return Result.failure(apiError("Respons server kosong"))
		return Result.success(body.wallets.map { it.data.toDomain() })
	}

	private fun Response<DeleteWalletResponseDto>.toUnitResult(): Result<Unit> {
		if (!isSuccessful) {
			return Result.failure(apiError(message = errorMessage()))
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
