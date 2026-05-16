package id.my.rizalanggoro.arta.feature.wallet.data

import id.my.rizalanggoro.arta.feature.wallet.data.dto.CreateWalletRequestDto
import id.my.rizalanggoro.arta.feature.wallet.data.dto.DeleteWalletResponseDto
import id.my.rizalanggoro.arta.feature.wallet.data.dto.UpdateWalletRequestDto
import id.my.rizalanggoro.arta.feature.wallet.data.dto.WalletListResponseDto
import id.my.rizalanggoro.arta.feature.wallet.data.dto.WalletResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.POST
import retrofit2.http.PUT

interface WalletApiService {
	@GET("api/wallet")
	suspend fun list(
		@Header("Authorization") authorization: String,
	): Response<WalletListResponseDto>

	@POST("api/wallet")
	suspend fun create(
		@Header("Authorization") authorization: String,
		@Body request: CreateWalletRequestDto,
	): Response<WalletResponseDto>

	@GET("api/wallet/{id}")
	suspend fun get(
		@Header("Authorization") authorization: String,
		@Path("id") id: Int,
	): Response<WalletResponseDto>

	@PUT("api/wallet/{id}")
	suspend fun update(
		@Header("Authorization") authorization: String,
		@Path("id") id: Int,
		@Body request: UpdateWalletRequestDto,
	): Response<WalletResponseDto>

	@DELETE("api/wallet/{id}")
	suspend fun delete(
		@Header("Authorization") authorization: String,
		@Path("id") id: Int,
	): Response<DeleteWalletResponseDto>
}
