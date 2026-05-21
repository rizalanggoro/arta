package id.my.rizalanggoro.arta.feature.gold.data

import id.my.rizalanggoro.arta.feature.gold.data.dto.CreateGoldRequestDto
import id.my.rizalanggoro.arta.feature.gold.data.dto.DeleteResponseDto
import id.my.rizalanggoro.arta.feature.gold.data.dto.GoldListResponseDto
import id.my.rizalanggoro.arta.feature.gold.data.dto.GoldResponseDto
import id.my.rizalanggoro.arta.feature.gold.data.dto.GoldTaxPreferenceRequestDto
import id.my.rizalanggoro.arta.feature.gold.data.dto.GoldTaxPreferenceResponseDto
import id.my.rizalanggoro.arta.feature.gold.data.dto.GoldTaxPreferencesResponseDto
import id.my.rizalanggoro.arta.feature.gold.data.dto.UpdateGoldRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.PUT

interface GoldApiService {
	@POST("api/gold")
	suspend fun create(
		@Header("Authorization") authorization: String,
		@Body request: CreateGoldRequestDto,
	): Response<GoldResponseDto>

	@GET("api/gold/{id}")
	suspend fun get(
		@Header("Authorization") authorization: String,
		@Path("id") id: Int,
	): Response<GoldResponseDto>

	@PUT("api/gold/{id}")
	suspend fun update(
		@Header("Authorization") authorization: String,
		@Path("id") id: Int,
		@Body request: UpdateGoldRequestDto,
	): Response<GoldResponseDto>

	@DELETE("api/gold/{id}")
	suspend fun delete(
		@Header("Authorization") authorization: String,
		@Path("id") id: Int,
	): Response<DeleteResponseDto>

	@GET("api/gold")
	suspend fun list(
		@Header("Authorization") authorization: String,
	): Response<GoldListResponseDto>

	@GET("api/gold/tax")
	suspend fun getTaxPreferences(
		@Header("Authorization") authorization: String,
	): Response<GoldTaxPreferencesResponseDto>

	@POST("api/gold/tax")
	suspend fun createTaxPreference(
		@Header("Authorization") authorization: String,
		@Body request: GoldTaxPreferenceRequestDto,
	): Response<GoldTaxPreferenceResponseDto>

	@PUT("api/gold/tax/{id}")
	suspend fun updateTaxPreference(
		@Header("Authorization") authorization: String,
		@Path("id") id: Int,
		@Body request: GoldTaxPreferenceRequestDto,
	): Response<GoldTaxPreferenceResponseDto>

	@DELETE("api/gold/tax/{id}")
	suspend fun deleteTaxPreference(
		@Header("Authorization") authorization: String,
		@Path("id") id: Int,
	): Response<DeleteResponseDto>
}
