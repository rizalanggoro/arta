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
import id.my.rizalanggoro.arta.openapi.models.GetCategoryRes
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
     * @return [CreateCategoryRes]
     */
    @POST("api/category")
    suspend fun createCategory(@Header("Authorization") authorization: kotlin.String, @Body body: CategoryCreateCategoryReq): Response<CreateCategoryRes>

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
     * GET api/category/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param authorization Bearer token
     * @param id category id
     * @return [GetCategoryRes]
     */
    @GET("api/category/{id}")
    suspend fun getCategory(@Header("Authorization") authorization: kotlin.String, @Path("id") id: kotlin.Int): Response<GetCategoryRes>

    /**
     * GET api/category
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param authorization Bearer token
     * @return [CategoryListCategoriesRes]
     */
    @GET("api/category")
    suspend fun listCategories(@Header("Authorization") authorization: kotlin.String): Response<CategoryListCategoriesRes>

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
