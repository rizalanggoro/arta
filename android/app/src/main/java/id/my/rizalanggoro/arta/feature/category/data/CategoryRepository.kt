package id.my.rizalanggoro.arta.feature.category.data

import id.my.rizalanggoro.arta.core.dto.ApiErrorDto
import id.my.rizalanggoro.arta.domain.AuthSession
import id.my.rizalanggoro.arta.domain.Category
import id.my.rizalanggoro.arta.feature.category.data.dto.CategoryListResponseDto
import id.my.rizalanggoro.arta.feature.category.data.dto.CreateCategoryRequestDto
import id.my.rizalanggoro.arta.feature.category.data.dto.DeleteCategoryResponseDto
import id.my.rizalanggoro.arta.feature.category.data.dto.CategoryResponseDto
import id.my.rizalanggoro.arta.feature.category.data.dto.UpdateCategoryRequestDto
import id.my.rizalanggoro.arta.feature.category.data.mapper.toDomain
import kotlinx.serialization.json.Json
import retrofit2.Response

class CategoryRepository(
	private val apiService: CategoryApiService,
	private val authSessionProvider: () -> AuthSession?,
) {
	private val json = Json { ignoreUnknownKeys = true }

	suspend fun createCategory(
		name: String,
		type: String,
	): Result<Category> {
		return runCatching {
			val authorization = authorizationHeader()
				?: throw apiError("Sesi login tidak ditemukan")

			apiService.create(
				authorization = authorization,
				request = CreateCategoryRequestDto(
					name = name,
					type = type,
				),
			).toDomainResult()
		}.getOrElse { Result.failure(it) }
	}

	suspend fun getCategories(type: String? = null): Result<List<Category>> {
		return runCatching {
			val authorization = authorizationHeader()
				?: throw apiError("Sesi login tidak ditemukan")

			apiService.list(authorization, type = type).toListResult()
		}.getOrElse { Result.failure(it) }
	}

	suspend fun getCategoryById(id: Int): Result<Category> {
		return runCatching {
			val authorization = authorizationHeader()
				?: throw apiError("Sesi login tidak ditemukan")

			apiService.get(authorization, id).toDomainResult()
		}.getOrElse { Result.failure(it) }
	}

	suspend fun updateCategory(
		id: Int,
		name: String,
		type: String,
	): Result<Category> {
		return runCatching {
			val authorization = authorizationHeader()
				?: throw apiError("Sesi login tidak ditemukan")

			apiService.update(
				authorization = authorization,
				id = id,
				request = UpdateCategoryRequestDto(
					name = name,
					type = type,
				),
			).toDomainResult()
		}.getOrElse { Result.failure(it) }
	}

	suspend fun deleteCategory(id: Int): Result<Unit> {
		return runCatching {
			val authorization = authorizationHeader()
				?: throw apiError("Sesi login tidak ditemukan")

			apiService.delete(authorization, id).toUnitResult()
		}.getOrElse { Result.failure(it) }
	}

	private fun authorizationHeader(): String? {
		val session = authSessionProvider()
		return session?.let { "Bearer ${it.token}" }
	}

	private fun Response<CategoryResponseDto>.toDomainResult(): Result<Category> {
		if (!isSuccessful) {
			return Result.failure(apiError(message = errorMessage()))
		}

		val body = body() ?: return Result.failure(apiError("Respons server kosong"))
		return Result.success(body.data.toDomain())
	}

	private fun Response<*>.errorMessage(): String {
		val errorBody = errorBody()?.string().orEmpty()
		val message = runCatching {
			json.decodeFromString(ApiErrorDto.serializer(), errorBody).message
		}.getOrNull()

		return when {
			!message.isNullOrBlank() -> message
			code() in 400..499 -> "Permintaan tidak valid"
			code() >= 500 -> "Terjadi kesalahan pada server"
			else -> "Terjadi kesalahan tidak diketahui"
		}
	}

	private fun Response<CategoryListResponseDto>.toListResult(): Result<List<Category>> {
		if (!isSuccessful) {
			return Result.failure(apiError(message = errorMessage()))
		}

		val body = body() ?: return Result.failure(apiError("Respons server kosong"))
		return Result.success(body.categories.map { it.data.toDomain() })
	}

	private fun Response<DeleteCategoryResponseDto>.toUnitResult(): Result<Unit> {
		if (!isSuccessful) {
			return Result.failure(apiError(message = errorMessage()))
		}

		return Result.success(Unit)
	}

	private fun apiError(message: String): Throwable {
		return IllegalStateException(message)
	}
}