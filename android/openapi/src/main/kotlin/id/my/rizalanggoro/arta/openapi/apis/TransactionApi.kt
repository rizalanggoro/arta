package id.my.rizalanggoro.arta.openapi.apis

import id.my.rizalanggoro.arta.openapi.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

import id.my.rizalanggoro.arta.openapi.models.CreateTransactionReq
import id.my.rizalanggoro.arta.openapi.models.CreateTransactionRes
import id.my.rizalanggoro.arta.openapi.models.DeleteTransactionRes
import id.my.rizalanggoro.arta.openapi.models.DtoError
import id.my.rizalanggoro.arta.openapi.models.DtoTransaction
import id.my.rizalanggoro.arta.openapi.models.GetTransactionRes
import id.my.rizalanggoro.arta.openapi.models.UpdateTransactionReq
import id.my.rizalanggoro.arta.openapi.models.UpdateTransactionRes

interface TransactionApi {
    /**
     * POST api/transaction
     * 
     * 
     * Responses:
     *  - 201: Created
     *  - 400: Bad Request
     *  - 401: Unauthorized
     *  - 500: Internal Server Error
     *
     * @param authorization Bearer token
     * @param body body
     * @param idempotencyKey Unique key per submission attempt for safe retry (UUID recommended) (optional)
     * @return [CreateTransactionRes]
     */
    @POST("api/transaction")
    suspend fun createTransaction(@Header("Authorization") authorization: kotlin.String, @Body body: CreateTransactionReq, @Header("Idempotency-Key") idempotencyKey: kotlin.String? = null): Response<CreateTransactionRes>

    /**
     * DELETE api/transaction/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *  - 400: Bad Request
     *  - 401: Unauthorized
     *  - 404: Not Found
     *  - 500: Internal Server Error
     *
     * @param authorization Bearer token
     * @param id transaction id
     * @return [DeleteTransactionRes]
     */
    @DELETE("api/transaction/{id}")
    suspend fun deleteTransaction(@Header("Authorization") authorization: kotlin.String, @Path("id") id: kotlin.Int): Response<DeleteTransactionRes>

    /**
     * GET api/transaction/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *  - 400: Bad Request
     *  - 401: Unauthorized
     *  - 404: Not Found
     *  - 500: Internal Server Error
     *
     * @param authorization Bearer token
     * @param id transaction id
     * @return [GetTransactionRes]
     */
    @GET("api/transaction/{id}")
    suspend fun getTransaction(@Header("Authorization") authorization: kotlin.String, @Path("id") id: kotlin.Int): Response<GetTransactionRes>

    /**
     * GET api/transaction
     * 
     * 
     * Responses:
     *  - 200: OK
     *  - 400: Bad Request
     *  - 401: Unauthorized
     *  - 500: Internal Server Error
     *
     * @param authorization Bearer token
     * @param walletId wallet id
     * @param includeCategory include_category (optional)
     * @param startDate start_date (optional)
     * @param endDate end_date (optional)
     * @return [kotlin.collections.List<DtoTransaction>]
     */
    @GET("api/transaction")
    suspend fun listTransactions(@Header("Authorization") authorization: kotlin.String, @Query("wallet_id") walletId: kotlin.Int, @Query("include_category") includeCategory: kotlin.Boolean? = null, @Query("start_date") startDate: kotlin.String? = null, @Query("end_date") endDate: kotlin.String? = null): Response<kotlin.collections.List<DtoTransaction>>

    /**
     * PUT api/transaction/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *  - 400: Bad Request
     *  - 401: Unauthorized
     *  - 404: Not Found
     *  - 500: Internal Server Error
     *
     * @param authorization Bearer token
     * @param id transaction id
     * @param body body
     * @return [UpdateTransactionRes]
     */
    @PUT("api/transaction/{id}")
    suspend fun updateTransaction(@Header("Authorization") authorization: kotlin.String, @Path("id") id: kotlin.Int, @Body body: UpdateTransactionReq): Response<UpdateTransactionRes>

}
