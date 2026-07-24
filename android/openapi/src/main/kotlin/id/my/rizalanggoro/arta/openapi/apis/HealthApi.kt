package id.my.rizalanggoro.arta.openapi.apis

import id.my.rizalanggoro.arta.openapi.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

import id.my.rizalanggoro.arta.openapi.models.HealthHealthRes

interface HealthApi {
    /**
     * GET api/health
     * Health check
     * Return server health status and app version.
     * Responses:
     *  - 200: OK
     *
     * @return [HealthHealthRes]
     */
    @GET("api/health")
    suspend fun apiHealthGet(): Response<HealthHealthRes>

}
