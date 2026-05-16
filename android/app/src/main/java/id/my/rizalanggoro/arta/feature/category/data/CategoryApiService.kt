package id.my.rizalanggoro.arta.feature.category.data

import id.my.rizalanggoro.arta.feature.category.data.dto.CategoryListResponseDto
import id.my.rizalanggoro.arta.feature.category.data.dto.CategoryResponseDto
import id.my.rizalanggoro.arta.feature.category.data.dto.CreateCategoryRequestDto
import id.my.rizalanggoro.arta.feature.category.data.dto.DeleteCategoryResponseDto
import id.my.rizalanggoro.arta.feature.category.data.dto.UpdateCategoryRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.POST
import retrofit2.http.PUT

interface CategoryApiService {
	@GET("api/category")
	suspend fun list(
		@Header("Authorization") authorization: String,
	): Response<CategoryListResponseDto>

	@POST("api/category")
	suspend fun create(
		@Header("Authorization") authorization: String,
		@Body request: CreateCategoryRequestDto,
	): Response<CategoryResponseDto>

	@GET("api/category/{id}")
	suspend fun get(
		@Header("Authorization") authorization: String,
		@Path("id") id: Int,
	): Response<CategoryResponseDto>

	@PUT("api/category/{id}")
	suspend fun update(
		@Header("Authorization") authorization: String,
		@Path("id") id: Int,
		@Body request: UpdateCategoryRequestDto,
	): Response<CategoryResponseDto>

	@DELETE("api/category/{id}")
	suspend fun delete(
		@Header("Authorization") authorization: String,
		@Path("id") id: Int,
	): Response<DeleteCategoryResponseDto>
}