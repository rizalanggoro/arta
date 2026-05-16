package id.my.rizalanggoro.arta.feature.auth.data

import id.my.rizalanggoro.arta.feature.auth.data.dto.AuthResponseDto
import id.my.rizalanggoro.arta.feature.auth.data.dto.LoginRequestDto
import id.my.rizalanggoro.arta.feature.auth.data.dto.RegisterRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequestDto): Response<AuthResponseDto>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequestDto): Response<AuthResponseDto>
}