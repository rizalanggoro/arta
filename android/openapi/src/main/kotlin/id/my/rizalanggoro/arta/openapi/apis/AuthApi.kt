package id.my.rizalanggoro.arta.openapi.apis

import id.my.rizalanggoro.arta.openapi.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

import id.my.rizalanggoro.arta.openapi.models.DtoError
import id.my.rizalanggoro.arta.openapi.models.LoginReq
import id.my.rizalanggoro.arta.openapi.models.LoginRes
import id.my.rizalanggoro.arta.openapi.models.LogoutRes
import id.my.rizalanggoro.arta.openapi.models.MeRes
import id.my.rizalanggoro.arta.openapi.models.RegisterReq
import id.my.rizalanggoro.arta.openapi.models.RegisterRes

interface AuthApi {
    /**
     * POST api/auth/login
     * Login a user
     * Validate credentials and issue a session token.
     * Responses:
     *  - 200: OK
     *  - 400: Bad Request
     *  - 401: Unauthorized
     *  - 500: Internal Server Error
     *
     * @param body body
     * @return [LoginRes]
     */
    @POST("api/auth/login")
    suspend fun apiAuthLoginPost(@Body body: LoginReq): Response<LoginRes>

    /**
     * GET api/auth/me
     * Get current user
     * Return the authenticated user&#39;s profile.
     * Responses:
     *  - 200: OK
     *  - 401: Unauthorized
     *  - 500: Internal Server Error
     *
     * @param authorization Bearer token
     * @return [MeRes]
     */
    @GET("api/auth/me")
    suspend fun apiAuthMeGet(@Header("Authorization") authorization: kotlin.String): Response<MeRes>

    /**
     * POST api/auth/register
     * Register a new user
     * Create a new account and issue the first session token.
     * Responses:
     *  - 201: Created
     *  - 400: Bad Request
     *  - 409: Conflict
     *  - 500: Internal Server Error
     *
     * @param body body
     * @return [RegisterRes]
     */
    @POST("api/auth/register")
    suspend fun apiAuthRegisterPost(@Body body: RegisterReq): Response<RegisterRes>

    /**
     * POST api/auth/logout
     * Logout current session
     * Delete the current session token.
     * Responses:
     *  - 200: OK
     *  - 401: Unauthorized
     *  - 500: Internal Server Error
     *
     * @param authorization Bearer token
     * @return [LogoutRes]
     */
    @POST("api/auth/logout")
    suspend fun logout(@Header("Authorization") authorization: kotlin.String): Response<LogoutRes>

}
