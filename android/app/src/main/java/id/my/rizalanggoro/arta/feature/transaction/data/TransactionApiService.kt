package id.my.rizalanggoro.arta.feature.transaction.data

import id.my.rizalanggoro.arta.feature.transaction.data.dto.CreateTransactionRequestDto
import id.my.rizalanggoro.arta.feature.transaction.data.dto.TransactionListResponseDto
import id.my.rizalanggoro.arta.feature.transaction.data.dto.TransactionResponseDto
import id.my.rizalanggoro.arta.feature.transaction.data.dto.UpdateTransactionRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface TransactionApiService {
    @POST("/api/transaction")
    suspend fun create(
        @Header("Authorization") authorization: String,
        @Body request: CreateTransactionRequestDto,
    ): Response<TransactionResponseDto>

    @GET("/api/transaction/{id}")
    suspend fun get(
        @Header("Authorization") authorization: String,
        @Path("id") id: Int,
    ): Response<TransactionResponseDto>

    @PUT("/api/transaction/{id}")
    suspend fun update(
        @Header("Authorization") authorization: String,
        @Path("id") id: Int,
        @Body request: UpdateTransactionRequestDto,
    ): Response<TransactionResponseDto>

    @DELETE("/api/transaction/{id}")
    suspend fun delete(
        @Header("Authorization") authorization: String,
        @Path("id") id: Int,
    ): Response<Unit>

    @GET("/api/transaction")
    suspend fun list(
        @Header("Authorization") authorization: String,
        @Query("wallet_id") walletId: Int,
    ): Response<TransactionListResponseDto>
}
