package id.my.rizalanggoro.arta.feature.wallet.data

import id.my.rizalanggoro.arta.core.extension.errorMessage
import id.my.rizalanggoro.arta.domain.AuthSession
import id.my.rizalanggoro.arta.domain.Wallet
import id.my.rizalanggoro.arta.feature.wallet.data.dto.DeleteWalletResponseDto
import id.my.rizalanggoro.arta.feature.wallet.data.dto.UpdateWalletRequestDto
import id.my.rizalanggoro.arta.feature.wallet.data.dto.CreateWalletRequestDto
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
		return runCatching {
			val authorization = authorizationHeader()
				?: throw apiError("Sesi login tidak ditemukan")

			apiService.list(authorization).toListResult()
		}.getOrElse { Result.failure(it) }
	}

	suspend fun getWalletById(id: Int): Result<Wallet> {
		return runCatching {
			val authorization = authorizationHeader()
				?: throw apiError("Sesi login tidak ditemukan")

			apiService.get(authorization, id).toDomainResult()
		}.getOrElse { Result.failure(it) }
	}

	suspend fun updateWallet(
		id: Int,
		name: String,
		type: String,
	): Result<Wallet> {
		return runCatching {
			val authorization = authorizationHeader()
				?: throw apiError("Sesi login tidak ditemukan")

			apiService.update(
				authorization = authorization,
				id = id,
				request = UpdateWalletRequestDto(
					name = name,
					type = type,
				),
			).toDomainResult()
		}.getOrElse { Result.failure(it) }
	}

	suspend fun createWallet(
		name: String,
		type: String,
	): Result<Wallet> {
		return runCatching {
			val authorization = authorizationHeader()
				?: throw apiError("Sesi login tidak ditemukan")

			apiService.create(
				authorization = authorization,
				request = CreateWalletRequestDto(name = name, type = type),
			).toDomainResult()
		}.getOrElse { Result.failure(it) }
	}

	suspend fun deleteWallet(id: Int): Result<Unit> {
		return runCatching {
			val authorization = authorizationHeader()
				?: throw apiError("Sesi login tidak ditemukan")

			apiService.delete(authorization, id).toUnitResult()
		}.getOrElse { Result.failure(it) }
	}

	private fun authorizationHeader(): String? {
		val session = authSessionProvider()
		return session?.let { "Bearer ${it.token}" }
	}

	private fun Response<WalletResponseDto>.toDomainResult(): Result<Wallet> {
		if (!isSuccessful) {
			return Result.failure(apiError(message = errorMessage(json)))
		}

		val body = body() ?: return Result.failure(apiError("Respons server kosong"))
		return Result.success(body.data.toDomain())
	}

	private fun Response<WalletListResponseDto>.toListResult(): Result<List<Wallet>> {
		if (!isSuccessful) {
			return Result.failure(apiError(message = errorMessage(json)))
		}

		val body = body() ?: return Result.failure(apiError("Respons server kosong"))
		return Result.success(body.wallets.map { it.data.toDomain() })
	}

	private fun Response<DeleteWalletResponseDto>.toUnitResult(): Result<Unit> {
		if (!isSuccessful) {
			return Result.failure(apiError(message = errorMessage(json)))
		}

		return Result.success(Unit)
	}

	private fun apiError(message: String): Throwable {
		return IllegalStateException(message)
	}
}
