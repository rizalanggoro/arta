# CategoryApi

All URIs are relative to *http://localhost:8080*

| Method | HTTP request | Description |
| ------------- | ------------- | ------------- |
| [**createCategory**](CategoryApi.md#createCategory) | **POST** api/category |  |
| [**deleteCategory**](CategoryApi.md#deleteCategory) | **DELETE** api/category/{id} |  |
| [**getCategory**](CategoryApi.md#getCategory) | **GET** api/category/{category_id} |  |
| [**listCategories**](CategoryApi.md#listCategories) | **GET** api/category |  |
| [**updateCategory**](CategoryApi.md#updateCategory) | **PUT** api/category/{id} |  |





### Example
```kotlin
// Import classes:
//import id.my.rizalanggoro.arta.openapi.*
//import id.my.rizalanggoro.arta.openapi.infrastructure.*
//import id.my.rizalanggoro.arta.openapi.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(CategoryApi::class.java)
val authorization : kotlin.String = authorization_example // kotlin.String | Bearer token
val body : CategoryCreateCategoryReq =  // CategoryCreateCategoryReq | body

launch(Dispatchers.IO) {
    val result : CreateCategoryRes = webService.createCategory(authorization, body)
}
```

### Parameters
| **authorization** | **kotlin.String**| Bearer token | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **body** | [**CategoryCreateCategoryReq**](CategoryCreateCategoryReq.md)| body | |

### Return type

[**CreateCategoryRes**](CreateCategoryRes.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json




### Example
```kotlin
// Import classes:
//import id.my.rizalanggoro.arta.openapi.*
//import id.my.rizalanggoro.arta.openapi.infrastructure.*
//import id.my.rizalanggoro.arta.openapi.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(CategoryApi::class.java)
val authorization : kotlin.String = authorization_example // kotlin.String | Bearer token
val id : kotlin.Int = 56 // kotlin.Int | category id

launch(Dispatchers.IO) {
    val result : DeleteCategoryRes = webService.deleteCategory(authorization, id)
}
```

### Parameters
| **authorization** | **kotlin.String**| Bearer token | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **id** | **kotlin.Int**| category id | |

### Return type

[**DeleteCategoryRes**](DeleteCategoryRes.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json




### Example
```kotlin
// Import classes:
//import id.my.rizalanggoro.arta.openapi.*
//import id.my.rizalanggoro.arta.openapi.infrastructure.*
//import id.my.rizalanggoro.arta.openapi.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(CategoryApi::class.java)
val authorization : kotlin.String = authorization_example // kotlin.String | Bearer token
val categoryId : kotlin.Int = 56 // kotlin.Int | category id
val walletId : kotlin.Int = 56 // kotlin.Int | wallet_id
val includeTotalAmount : kotlin.Boolean = true // kotlin.Boolean | include_total_amount
val includeTransactions : kotlin.Boolean = true // kotlin.Boolean | include_transactions
val startDate : kotlin.String = startDate_example // kotlin.String | start_date
val endDate : kotlin.String = endDate_example // kotlin.String | end_date

launch(Dispatchers.IO) {
    val result : DtoCategory = webService.getCategory(authorization, categoryId, walletId, includeTotalAmount, includeTransactions, startDate, endDate)
}
```

### Parameters
| **authorization** | **kotlin.String**| Bearer token | |
| **categoryId** | **kotlin.Int**| category id | |
| **walletId** | **kotlin.Int**| wallet_id | [optional] |
| **includeTotalAmount** | **kotlin.Boolean**| include_total_amount | [optional] |
| **includeTransactions** | **kotlin.Boolean**| include_transactions | [optional] |
| **startDate** | **kotlin.String**| start_date | [optional] |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **endDate** | **kotlin.String**| end_date | [optional] |

### Return type

[**DtoCategory**](DtoCategory.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json




### Example
```kotlin
// Import classes:
//import id.my.rizalanggoro.arta.openapi.*
//import id.my.rizalanggoro.arta.openapi.infrastructure.*
//import id.my.rizalanggoro.arta.openapi.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(CategoryApi::class.java)
val authorization : kotlin.String = authorization_example // kotlin.String | Bearer token

launch(Dispatchers.IO) {
    val result : CategoryListCategoriesRes = webService.listCategories(authorization)
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **authorization** | **kotlin.String**| Bearer token | |

### Return type

[**CategoryListCategoriesRes**](CategoryListCategoriesRes.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json




### Example
```kotlin
// Import classes:
//import id.my.rizalanggoro.arta.openapi.*
//import id.my.rizalanggoro.arta.openapi.infrastructure.*
//import id.my.rizalanggoro.arta.openapi.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(CategoryApi::class.java)
val authorization : kotlin.String = authorization_example // kotlin.String | Bearer token
val id : kotlin.Int = 56 // kotlin.Int | category id
val body : CategoryUpdateCategoryReq =  // CategoryUpdateCategoryReq | body

launch(Dispatchers.IO) {
    val result : UpdateCategoryRes = webService.updateCategory(authorization, id, body)
}
```

### Parameters
| **authorization** | **kotlin.String**| Bearer token | |
| **id** | **kotlin.Int**| category id | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **body** | [**CategoryUpdateCategoryReq**](CategoryUpdateCategoryReq.md)| body | |

### Return type

[**UpdateCategoryRes**](UpdateCategoryRes.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

