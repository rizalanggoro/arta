package id.my.rizalanggoro.arta.openapi.apis

import id.my.rizalanggoro.arta.openapi.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

import id.my.rizalanggoro.arta.openapi.models.CreateGoldRes
import id.my.rizalanggoro.arta.openapi.models.CreateGoldTaxPreferenceRes
import id.my.rizalanggoro.arta.openapi.models.DeleteGoldRes
import id.my.rizalanggoro.arta.openapi.models.DeleteGoldTaxPreferenceRes
import id.my.rizalanggoro.arta.openapi.models.GetGoldRes
import id.my.rizalanggoro.arta.openapi.models.GoldCreateGoldReq
import id.my.rizalanggoro.arta.openapi.models.GoldGoldTaxPreferenceReq
import id.my.rizalanggoro.arta.openapi.models.GoldListGoldsRes
import id.my.rizalanggoro.arta.openapi.models.GoldSummaryRes
import id.my.rizalanggoro.arta.openapi.models.GoldUpdateGoldReq
import id.my.rizalanggoro.arta.openapi.models.ListGoldTaxPreferencesRes
import id.my.rizalanggoro.arta.openapi.models.UpdateGoldRes
import id.my.rizalanggoro.arta.openapi.models.UpdateGoldTaxPreferenceRes

interface GoldApi {
    /**
     * POST api/gold
     * 
     * 
     * Responses:
     *  - 201: Created
     *
     * @param authorization Bearer token
     * @param body body
     * @return [CreateGoldRes]
     */
    @POST("api/gold")
    suspend fun createGold(@Header("Authorization") authorization: kotlin.String, @Body body: GoldCreateGoldReq): Response<CreateGoldRes>

    /**
     * POST api/gold/tax
     * 
     * 
     * Responses:
     *  - 201: Created
     *
     * @param authorization Bearer token
     * @param body body
     * @return [CreateGoldTaxPreferenceRes]
     */
    @POST("api/gold/tax")
    suspend fun createGoldTaxPreference(@Header("Authorization") authorization: kotlin.String, @Body body: GoldGoldTaxPreferenceReq): Response<CreateGoldTaxPreferenceRes>

    /**
     * DELETE api/gold/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param authorization Bearer token
     * @param id gold id
     * @return [DeleteGoldRes]
     */
    @DELETE("api/gold/{id}")
    suspend fun deleteGold(@Header("Authorization") authorization: kotlin.String, @Path("id") id: kotlin.Int): Response<DeleteGoldRes>

    /**
     * DELETE api/gold/tax/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param authorization Bearer token
     * @param id tax preference id
     * @return [DeleteGoldTaxPreferenceRes]
     */
    @DELETE("api/gold/tax/{id}")
    suspend fun deleteGoldTaxPreference(@Header("Authorization") authorization: kotlin.String, @Path("id") id: kotlin.Int): Response<DeleteGoldTaxPreferenceRes>

    /**
     * GET api/gold/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param authorization Bearer token
     * @param id gold id
     * @return [GetGoldRes]
     */
    @GET("api/gold/{id}")
    suspend fun getGold(@Header("Authorization") authorization: kotlin.String, @Path("id") id: kotlin.Int): Response<GetGoldRes>

    /**
     * GET api/gold/summary
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param authorization Bearer token
     * @return [GoldSummaryRes]
     */
    @GET("api/gold/summary")
    suspend fun goldSummary(@Header("Authorization") authorization: kotlin.String): Response<GoldSummaryRes>

    /**
     * GET api/gold/tax
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param authorization Bearer token
     * @return [ListGoldTaxPreferencesRes]
     */
    @GET("api/gold/tax")
    suspend fun listGoldTaxPreferences(@Header("Authorization") authorization: kotlin.String): Response<ListGoldTaxPreferencesRes>

    /**
     * GET api/gold
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param authorization Bearer token
     * @return [GoldListGoldsRes]
     */
    @GET("api/gold")
    suspend fun listGolds(@Header("Authorization") authorization: kotlin.String): Response<GoldListGoldsRes>

    /**
     * PUT api/gold/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param authorization Bearer token
     * @param id gold id
     * @param body body
     * @return [UpdateGoldRes]
     */
    @PUT("api/gold/{id}")
    suspend fun updateGold(@Header("Authorization") authorization: kotlin.String, @Path("id") id: kotlin.Int, @Body body: GoldUpdateGoldReq): Response<UpdateGoldRes>

    /**
     * PUT api/gold/tax/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param authorization Bearer token
     * @param id tax preference id
     * @param body body
     * @return [UpdateGoldTaxPreferenceRes]
     */
    @PUT("api/gold/tax/{id}")
    suspend fun updateGoldTaxPreference(@Header("Authorization") authorization: kotlin.String, @Path("id") id: kotlin.Int, @Body body: GoldGoldTaxPreferenceReq): Response<UpdateGoldTaxPreferenceRes>

}
