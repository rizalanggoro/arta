package id.my.rizalanggoro.arta.feature.transaction.data

import id.my.rizalanggoro.arta.core.extension.errorMessage
import id.my.rizalanggoro.arta.domain.AuthSession
import id.my.rizalanggoro.arta.domain.Transaction
import id.my.rizalanggoro.arta.feature.transaction.data.dto.CreateTransactionRequestDto
import id.my.rizalanggoro.arta.feature.transaction.data.dto.TransactionListResponseDto
import id.my.rizalanggoro.arta.feature.transaction.data.dto.TransactionResponseDto
import id.my.rizalanggoro.arta.feature.transaction.data.dto.UpdateTransactionRequestDto
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
        amount: Double,
        categoryId: Int,
        description: String = "",
        date: String,
    ): Result<Transaction> {
        return runCatching {
            val authorization =
                authorizationHeader() ?: throw apiError("Sesi login tidak ditemukan")

            apiService.create(
                authorization = authorization,
                request = CreateTransactionRequestDto(
                    walletId = walletId,
                    amount = amount,
                    categoryId = categoryId,
                    description = description,
                    date = date,
                ),
            ).toDomainResult()
        }.fold(
            onSuccess = { it },
            onFailure = { Result.failure(it) },
        )
    }

    suspend fun getTransactionById(id: Int): Result<Transaction> {
        return runCatching {
            val authorization =
                authorizationHeader() ?: throw apiError("Sesi login tidak ditemukan")

            apiService.get(authorization = authorization, id = id).toDomainResult()
        }.fold(
            onSuccess = { it },
            onFailure = { Result.failure(it) },
        )
    }

    suspend fun listTransactionsByWallet(walletId: Int): Result<List<Transaction>> {
        return runCatching {
            val authorization =
                authorizationHeader() ?: throw apiError("Sesi login tidak ditemukan")

            apiService.list(authorization = authorization, walletId = walletId).toListDomainResult()
        }.fold(
            onSuccess = { it },
            onFailure = { Result.failure(it) },
        )
    }

    suspend fun updateTransaction(
        id: Int,
        walletId: Int? = null,
        amount: Double? = null,
        categoryId: Int? = null,
        description: String? = null,
        date: String? = null,
    ): Result<Transaction> {
        return runCatching {
            val authorization =
                authorizationHeader() ?: throw apiError("Sesi login tidak ditemukan")

            apiService.update(
                authorization = authorization,
                id = id,
                request = UpdateTransactionRequestDto(
                    walletId = walletId,
                    amount = amount,
                    categoryId = categoryId,
                    description = description,
                    date = date,
                ),
            ).toDomainResult()
        }.fold(
            onSuccess = { it },
            onFailure = { Result.failure(it) },
        )
    }

    suspend fun deleteTransaction(id: Int): Result<Unit> {
        return runCatching {
            val authorization =
                authorizationHeader() ?: throw apiError("Sesi login tidak ditemukan")

            val response = apiService.delete(authorization = authorization, id = id)
            if (!response.isSuccessful) {
                return@runCatching Result.failure(
                    apiError(
                        message = response.errorBody()?.string().orEmpty()
                    )
                )
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

    private fun Response<TransactionResponseDto>.toDomainResult(): Result<Transaction> {
        if (!isSuccessful) {
            return Result.failure(apiError(message = errorMessage(json)))
        }

        val body = body() ?: return Result.failure(apiError("Respons server kosong"))
        return Result.success(body.data.toDomain())
    }

    private fun Response<TransactionListResponseDto>.toListDomainResult(): Result<List<Transaction>> {
        if (!isSuccessful) {
            return Result.failure(apiError(message = errorMessage(json)))
        }

        val body = body() ?: return Result.failure(apiError("Respons server kosong"))
        return Result.success(body.transactions.map { it.data.toDomain() })
    }

    

    private fun apiError(message: String): Throwable {
        return IllegalStateException(message)
    }
}
