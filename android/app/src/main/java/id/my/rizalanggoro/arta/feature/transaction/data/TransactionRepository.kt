package id.my.rizalanggoro.arta.feature.transaction.data

import id.my.rizalanggoro.arta.core.dto.ApiErrorDto
import id.my.rizalanggoro.arta.domain.AuthSession
import id.my.rizalanggoro.arta.domain.Transaction
import id.my.rizalanggoro.arta.feature.transaction.data.dto.CreateTransactionRequestDto
import id.my.rizalanggoro.arta.feature.transaction.data.dto.UpdateTransactionRequestDto
import id.my.rizalanggoro.arta.feature.transaction.data.dto.TransactionResponseDto
import id.my.rizalanggoro.arta.feature.transaction.data.mapper.toDomain
import kotlinx.serialization.json.Json
import retrofit2.Response

class TransactionRepository(
    private val apiService: TransactionApiService,
    private val authSessionProvider: () -> AuthSession?,
) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun createTransaction(
        walletId: Int,
        type: String,
        amount: Double,
        categoryId: Int? = null,
        description: String = "",
        date: String,
    ): Result<Transaction> {
        val authorization = authorizationHeader() ?: return Result.failure(apiError("Sesi login tidak ditemukan"))

        return apiService.create(
            authorization = authorization,
            request = CreateTransactionRequestDto(
                walletId = walletId,
                type = type,
                amount = amount,
                categoryId = categoryId,
                description = description,
                date = date,
            ),
        ).toDomainResult()
    }

    private fun authorizationHeader(): String? {
        val session = authSessionProvider()
        return session?.let { "${it.tokenType} ${it.token}" }
    }

    private fun Response<TransactionResponseDto>.toDomainResult(): Result<Transaction> {
        if (!isSuccessful) {
            return Result.failure(apiError(message = errorMessage()))
        }

        val body = body() ?: return Result.failure(apiError("Respons server kosong"))
        return Result.success(body.data.toDomain())
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

    suspend fun getTransactionById(id: Int): Result<Transaction> {
        val authorization = authorizationHeader() ?: return Result.failure(apiError("Sesi login tidak ditemukan"))

        return apiService.get(authorization = authorization, id = id).toDomainResult()
    }

    suspend fun updateTransaction(
        id: Int,
        walletId: Int? = null,
        type: String? = null,
        amount: Double? = null,
        categoryId: Int? = null,
        description: String? = null,
        date: String? = null,
    ): Result<Transaction> {
        val authorization = authorizationHeader() ?: return Result.failure(apiError("Sesi login tidak ditemukan"))

        return apiService.update(
            authorization = authorization,
            id = id,
            request = UpdateTransactionRequestDto(
                walletId = walletId,
                type = type,
                amount = amount,
                categoryId = categoryId,
                description = description,
                date = date,
            ),
        ).toDomainResult()
    }

    suspend fun deleteTransaction(id: Int): Result<Unit> {
        val authorization = authorizationHeader() ?: return Result.failure(apiError("Sesi login tidak ditemukan"))
        val response = apiService.delete(authorization = authorization, id = id)
        if (!response.isSuccessful) {
            return Result.failure(apiError(message = response.errorBody()?.string().orEmpty()))
        }
        return Result.success(Unit)
    }
}
