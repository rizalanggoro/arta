package id.my.rizalanggoro.arta.feature.home.data

import id.my.rizalanggoro.arta.feature.home.data.dto.CashDashboardResponseDto
import id.my.rizalanggoro.arta.feature.home.data.dto.GoldDashboardResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface DashboardApiService {
    @GET("api/dashboard/cash")
    suspend fun getCashDashboard(
        @Header("Authorization") authorization: String,
        @Query("wallet_id") walletId: Int? = null,
    ): Response<CashDashboardResponseDto>

    @GET("api/dashboard/gold")
    suspend fun getGoldDashboard(
        @Header("Authorization") authorization: String,
    ): Response<GoldDashboardResponseDto>
}