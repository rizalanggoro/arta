package id.my.rizalanggoro.arta.feature.home.data

import id.my.rizalanggoro.arta.feature.home.data.dto.CashDashboardResponseDto
import id.my.rizalanggoro.arta.feature.home.data.dto.GoldDashboardResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface DashboardApiService {
    @GET("api/dashboard/cash")
    suspend fun getCashDashboard(
        @Header("Authorization") authorization: String,
    ): Response<CashDashboardResponseDto>

    @GET("api/dashboard/gold")
    suspend fun getGoldDashboard(
        @Header("Authorization") authorization: String,
    ): Response<GoldDashboardResponseDto>
}