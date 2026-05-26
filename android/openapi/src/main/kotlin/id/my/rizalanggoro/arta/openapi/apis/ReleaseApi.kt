package id.my.rizalanggoro.arta.openapi.apis

import id.my.rizalanggoro.arta.openapi.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

import id.my.rizalanggoro.arta.openapi.models.CreateReleaseReq
import id.my.rizalanggoro.arta.openapi.models.DtoError
import id.my.rizalanggoro.arta.openapi.models.ReleaseRes

interface ReleaseApi {
    /**
     * GET api/release/latest
     * Get latest release
     * Return the release with the highest version code.
     * Responses:
     *  - 200: OK
     *  - 404: Not Found
     *  - 500: Internal Server Error
     *
     * @return [ReleaseRes]
     */
    @GET("api/release/latest")
    suspend fun apiReleaseLatestGet(): Response<ReleaseRes>

    /**
     * POST api/release
     * Create a new release
     * Store a new APK release url and its version code.
     * Responses:
     *  - 201: Created
     *  - 400: Bad Request
     *  - 500: Internal Server Error
     *
     * @param body body
     * @return [ReleaseRes]
     */
    @POST("api/release")
    suspend fun apiReleasePost(@Body body: CreateReleaseReq): Response<ReleaseRes>

}
