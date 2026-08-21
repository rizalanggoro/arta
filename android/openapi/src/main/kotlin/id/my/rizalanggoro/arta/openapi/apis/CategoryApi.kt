package id.my.rizalanggoro.arta.openapi.apis

import id.my.rizalanggoro.arta.openapi.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

import id.my.rizalanggoro.arta.openapi.models.CategoryCreateCategoryReq
import id.my.rizalanggoro.arta.openapi.models.CategoryListCategoriesRes
import id.my.rizalanggoro.arta.openapi.models.CategoryUpdateCategoryReq
import id.my.rizalanggoro.arta.openapi.models.CreateCategoryRes
import id.my.rizalanggoro.arta.openapi.models.DeleteCategoryRes
import id.my.rizalanggoro.arta.openapi.models.DtoCategory
import id.my.rizalanggoro.arta.openapi.models.UpdateCategoryRes

interface CategoryApi {
    /**
     * POST api/category
     * 
     * 
     * Responses:
     *  - 201: Created
     *
     * @param authorization Bearer token
     * @param body body
     * @param idempotencyKey Unique key per submission attempt for safe retry (UUID recommended) (optional)
     * @return [CreateCategoryRes]
     */
    @POST("api/category")
    suspend fun createCategory(@Header("Authorization") authorization: kotlin.String, @Body body: CategoryCreateCategoryReq, @Header("Idempotency-Key") idempotencyKey: kotlin.String? = null): Response<CreateCategoryRes>

    /**
     * DELETE api/category/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param authorization Bearer token
     * @param id category id
     * @return [DeleteCategoryRes]
     */
    @DELETE("api/category/{id}")
    suspend fun deleteCategory(@Header("Authorization") authorization: kotlin.String, @Path("id") id: kotlin.Int): Response<DeleteCategoryRes>

    /**
     * GET api/category/{category_id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param authorization Bearer token
     * @param categoryId category id
     * @param walletId wallet_id (optional)
     * @param includeTotalAmount include_total_amount (optional)
     * @param includeTransactions include_transactions (optional)
     * @param startDate start_date (optional)
     * @param endDate end_date (optional)
     * @return [DtoCategory]
     */
    @GET("api/category/{category_id}")
    suspend fun getCategory(@Header("Authorization") authorization: kotlin.String, @Path("category_id") categoryId: kotlin.Int, @Query("wallet_id") walletId: kotlin.Int? = null, @Query("include_total_amount") includeTotalAmount: kotlin.Boolean? = null, @Query("include_transactions") includeTransactions: kotlin.Boolean? = null, @Query("start_date") startDate: kotlin.String? = null, @Query("end_date") endDate: kotlin.String? = null): Response<DtoCategory>

    /**
     * GET api/category
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param authorization Bearer token
     * @param type income or expense (optional)
     * @param walletId wallet_id (optional)
     * @param includeStats include transaction stats (optional)
     * @param startDate start_date (2006-01-02) (optional)
     * @param endDate end_date (2006-01-02) (optional)
     * @return [CategoryListCategoriesRes]
     */
    @GET("api/category")
    suspend fun listCategories(@Header("Authorization") authorization: kotlin.String, @Query("type") type: kotlin.String? = null, @Query("wallet_id") walletId: kotlin.Int? = null, @Query("include_stats") includeStats: kotlin.Boolean? = null, @Query("start_date") startDate: kotlin.String? = null, @Query("end_date") endDate: kotlin.String? = null): Response<CategoryListCategoriesRes>

    /**
     * PUT api/category/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param authorization Bearer token
     * @param id category id
     * @param body body
     * @return [UpdateCategoryRes]
     */
    @PUT("api/category/{id}")
    suspend fun updateCategory(@Header("Authorization") authorization: kotlin.String, @Path("id") id: kotlin.Int, @Body body: CategoryUpdateCategoryReq): Response<UpdateCategoryRes>

}
