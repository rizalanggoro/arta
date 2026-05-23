package id.my.rizalanggoro.arta.core.network

import id.my.rizalanggoro.arta.openapi.infrastructure.Serializer.kotlinxSerializationJson
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

object RetrofitProvider {
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(NetworkConfig.BASE_URL)
            .addConverterFactory(
                "application/json".toMediaType().let(
                    kotlinxSerializationJson::asConverterFactory
                )
            )
            .build()
    }

    fun <T> create(service: Class<T>): T = retrofit.create(service)
}