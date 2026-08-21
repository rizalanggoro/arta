package id.my.rizalanggoro.arta.openapi.apis

import id.my.rizalanggoro.arta.openapi.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

import id.my.rizalanggoro.arta.openapi.models.CashDashboardRes
import id.my.rizalanggoro.arta.openapi.models.DtoError
import id.my.rizalanggoro.arta.openapi.models.GoldDashboardRes
import id.my.rizalanggoro.arta.openapi.models.PriceHistoryRes

interface DashboardApi {
    /**
     * GET api/dashboard/cash
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
     * @param walletId wallet_id
     * @param startDate start_date
     * @param endDate end_date
     * @return [CashDashboardRes]
     */
    @GET("api/dashboard/cash")
    suspend fun getCashDashboard(@Header("Authorization") authorization: kotlin.String, @Query("wallet_id") walletId: kotlin.Int, @Query("start_date") startDate: kotlin.String, @Query("end_date") endDate: kotlin.String): Response<CashDashboardRes>

    /**
     * GET api/dashboard/gold
     * 
     * 
     * Responses:
     *  - 200: OK
     *  - 401: Unauthorized
     *  - 404: Not Found
     *  - 500: Internal Server Error
     *
     * @param authorization Bearer token
     * @param walletId wallet_id
     * @return [GoldDashboardRes]
     */
    @GET("api/dashboard/gold")
    suspend fun getGoldDashboard(@Header("Authorization") authorization: kotlin.String, @Query("wallet_id") walletId: kotlin.Int): Response<GoldDashboardRes>

    /**
     * GET api/dashboard/price-history
     * 
     * 
     * Responses:
     *  - 200: OK
     *  - 400: Bad Request
     *  - 401: Unauthorized
     *  - 500: Internal Server Error
     *
     * @param authorization Bearer token
     * @param type price type: gold or fx
     * @param days number of days of history (default 7) (optional)
     * @return [PriceHistoryRes]
     */
    @GET("api/dashboard/price-history")
    suspend fun getPriceHistory(@Header("Authorization") authorization: kotlin.String, @Query("type") type: kotlin.String, @Query("days") days: kotlin.Int? = null): Response<PriceHistoryRes>

}
