package id.my.rizalanggoro.arta.feature.gold.data

import id.my.rizalanggoro.arta.feature.gold.data.dto.CreateGoldRequestDto
import id.my.rizalanggoro.arta.feature.gold.data.dto.GoldResponseDto
import id.my.rizalanggoro.arta.feature.gold.data.dto.UpdateGoldRequestDto
import id.my.rizalanggoro.arta.feature.gold.data.dto.DeleteResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

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
}
