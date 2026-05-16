package id.my.rizalanggoro.arta.core.di

import id.my.rizalanggoro.arta.feature.auth.data.AuthApiService
import id.my.rizalanggoro.arta.feature.auth.data.AuthRepository
import id.my.rizalanggoro.arta.core.network.RetrofitProvider

class AppContainer {
	private val authApiService: AuthApiService = RetrofitProvider.create(AuthApiService::class.java)

	val authRepository: AuthRepository = AuthRepository(authApiService)
}