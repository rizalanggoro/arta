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

interface DashboardApi {
    /**
     * GET api/dashboard/cash
     * Get cash dashboard overview
     * Return the active cash wallet name, balance summary, today totals, and the latest 5 transactions.
     * Responses:
     *  - 200: OK
     *  - 400: Bad Request
     *  - 401: Unauthorized
     *  - 404: Not Found
     *  - 500: Internal Server Error
     *
     * @param authorization Bearer token
     * @param walletId Selected cash wallet ID (optional)
     * @return [CashDashboardRes]
     */
    @GET("api/dashboard/cash")
    suspend fun getCashDashboard(@Header("Authorization") authorization: kotlin.String, @Query("wallet_id") walletId: kotlin.Int? = null): Response<CashDashboardRes>

    /**
     * GET api/dashboard/gold
     * Get gold dashboard overview
     * Return the active gold wallet name, asset summary, current prices, and the latest 5 gold entries.
     * Responses:
     *  - 200: OK
     *  - 401: Unauthorized
     *  - 404: Not Found
     *  - 500: Internal Server Error
     *
     * @param authorization Bearer token
     * @return [GoldDashboardRes]
     */
    @GET("api/dashboard/gold")
    suspend fun getGoldDashboard(@Header("Authorization") authorization: kotlin.String): Response<GoldDashboardRes>

}
