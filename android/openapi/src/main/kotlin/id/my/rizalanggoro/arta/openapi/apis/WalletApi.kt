package id.my.rizalanggoro.arta.openapi.apis

import id.my.rizalanggoro.arta.openapi.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

import id.my.rizalanggoro.arta.openapi.models.CreateWalletRes
import id.my.rizalanggoro.arta.openapi.models.DeleteWalletRes
import id.my.rizalanggoro.arta.openapi.models.DtoError
import id.my.rizalanggoro.arta.openapi.models.GetWalletRes
import id.my.rizalanggoro.arta.openapi.models.UpdateWalletRes
import id.my.rizalanggoro.arta.openapi.models.WalletCreateWalletReq
import id.my.rizalanggoro.arta.openapi.models.WalletListWalletsRes
import id.my.rizalanggoro.arta.openapi.models.WalletUpdateWalletReq

interface WalletApi {
    /**
     * POST api/wallet
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
     * @return [CreateWalletRes]
     */
    @POST("api/wallet")
    suspend fun createWallet(@Header("Authorization") authorization: kotlin.String, @Body body: WalletCreateWalletReq): Response<CreateWalletRes>

    /**
     * DELETE api/wallet/{id}
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
     * @param id Wallet ID
     * @return [DeleteWalletRes]
     */
    @DELETE("api/wallet/{id}")
    suspend fun deleteWallet(@Header("Authorization") authorization: kotlin.String, @Path("id") id: kotlin.Int): Response<DeleteWalletRes>

    /**
     * GET api/wallet/{id}
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
     * @param id Wallet ID
     * @return [GetWalletRes]
     */
    @GET("api/wallet/{id}")
    suspend fun getWallet(@Header("Authorization") authorization: kotlin.String, @Path("id") id: kotlin.Int): Response<GetWalletRes>

    /**
     * GET api/wallet
     * 
     * 
     * Responses:
     *  - 200: OK
     *  - 401: Unauthorized
     *  - 500: Internal Server Error
     *
     * @param authorization Bearer token
     * @return [WalletListWalletsRes]
     */
    @GET("api/wallet")
    suspend fun listWallets(@Header("Authorization") authorization: kotlin.String): Response<WalletListWalletsRes>

    /**
     * PUT api/wallet/{id}
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
     * @param id Wallet ID
     * @param body body
     * @return [UpdateWalletRes]
     */
    @PUT("api/wallet/{id}")
    suspend fun updateWallet(@Header("Authorization") authorization: kotlin.String, @Path("id") id: kotlin.Int, @Body body: WalletUpdateWalletReq): Response<UpdateWalletRes>

}
