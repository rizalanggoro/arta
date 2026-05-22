# GoldApi

All URIs are relative to *http://localhost:3000/api*

| Method | HTTP request | Description |
| ------------- | ------------- | ------------- |
| [**createGold**](GoldApi.md#createGold) | **POST** api/gold |  |
| [**createGoldTaxPreference**](GoldApi.md#createGoldTaxPreference) | **POST** api/gold/tax |  |
| [**deleteGold**](GoldApi.md#deleteGold) | **DELETE** api/gold/{id} |  |
| [**deleteGoldTaxPreference**](GoldApi.md#deleteGoldTaxPreference) | **DELETE** api/gold/tax/{id} |  |
| [**getGold**](GoldApi.md#getGold) | **GET** api/gold/{id} |  |
| [**goldSummary**](GoldApi.md#goldSummary) | **GET** api/gold/summary |  |
| [**listGoldTaxPreferences**](GoldApi.md#listGoldTaxPreferences) | **GET** api/gold/tax |  |
| [**listGolds**](GoldApi.md#listGolds) | **GET** api/gold |  |
| [**updateGold**](GoldApi.md#updateGold) | **PUT** api/gold/{id} |  |
| [**updateGoldTaxPreference**](GoldApi.md#updateGoldTaxPreference) | **PUT** api/gold/tax/{id} |  |





### Example
```kotlin
// Import classes:
//import id.my.rizalanggoro.arta.openapi.*
//import id.my.rizalanggoro.arta.openapi.infrastructure.*
//import id.my.rizalanggoro.arta.openapi.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(GoldApi::class.java)
val authorization : kotlin.String = authorization_example // kotlin.String | Bearer token
val body : GoldCreateGoldReq =  // GoldCreateGoldReq | body

launch(Dispatchers.IO) {
    val result : CreateGoldRes = webService.createGold(authorization, body)
}
```

### Parameters
| **authorization** | **kotlin.String**| Bearer token | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **body** | [**GoldCreateGoldReq**](GoldCreateGoldReq.md)| body | |

### Return type

[**CreateGoldRes**](CreateGoldRes.md)

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
val webService = apiClient.createWebservice(GoldApi::class.java)
val authorization : kotlin.String = authorization_example // kotlin.String | Bearer token
val body : GoldGoldTaxPreferenceReq =  // GoldGoldTaxPreferenceReq | body

launch(Dispatchers.IO) {
    val result : CreateGoldTaxPreferenceRes = webService.createGoldTaxPreference(authorization, body)
}
```

### Parameters
| **authorization** | **kotlin.String**| Bearer token | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **body** | [**GoldGoldTaxPreferenceReq**](GoldGoldTaxPreferenceReq.md)| body | |

### Return type

[**CreateGoldTaxPreferenceRes**](CreateGoldTaxPreferenceRes.md)

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
val webService = apiClient.createWebservice(GoldApi::class.java)
val authorization : kotlin.String = authorization_example // kotlin.String | Bearer token
val id : kotlin.Int = 56 // kotlin.Int | gold id

launch(Dispatchers.IO) {
    val result : DeleteGoldRes = webService.deleteGold(authorization, id)
}
```

### Parameters
| **authorization** | **kotlin.String**| Bearer token | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **id** | **kotlin.Int**| gold id | |

### Return type

[**DeleteGoldRes**](DeleteGoldRes.md)

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
val webService = apiClient.createWebservice(GoldApi::class.java)
val authorization : kotlin.String = authorization_example // kotlin.String | Bearer token
val id : kotlin.Int = 56 // kotlin.Int | tax preference id

launch(Dispatchers.IO) {
    val result : DeleteGoldTaxPreferenceRes = webService.deleteGoldTaxPreference(authorization, id)
}
```

### Parameters
| **authorization** | **kotlin.String**| Bearer token | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **id** | **kotlin.Int**| tax preference id | |

### Return type

[**DeleteGoldTaxPreferenceRes**](DeleteGoldTaxPreferenceRes.md)

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
val webService = apiClient.createWebservice(GoldApi::class.java)
val authorization : kotlin.String = authorization_example // kotlin.String | Bearer token
val id : kotlin.Int = 56 // kotlin.Int | gold id

launch(Dispatchers.IO) {
    val result : GetGoldRes = webService.getGold(authorization, id)
}
```

### Parameters
| **authorization** | **kotlin.String**| Bearer token | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **id** | **kotlin.Int**| gold id | |

### Return type

[**GetGoldRes**](GetGoldRes.md)

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
val webService = apiClient.createWebservice(GoldApi::class.java)
val authorization : kotlin.String = authorization_example // kotlin.String | Bearer token

launch(Dispatchers.IO) {
    val result : GoldSummaryRes = webService.goldSummary(authorization)
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **authorization** | **kotlin.String**| Bearer token | |

### Return type

[**GoldSummaryRes**](GoldSummaryRes.md)

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
val webService = apiClient.createWebservice(GoldApi::class.java)
val authorization : kotlin.String = authorization_example // kotlin.String | Bearer token

launch(Dispatchers.IO) {
    val result : ListGoldTaxPreferencesRes = webService.listGoldTaxPreferences(authorization)
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **authorization** | **kotlin.String**| Bearer token | |

### Return type

[**ListGoldTaxPreferencesRes**](ListGoldTaxPreferencesRes.md)

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
val webService = apiClient.createWebservice(GoldApi::class.java)
val authorization : kotlin.String = authorization_example // kotlin.String | Bearer token

launch(Dispatchers.IO) {
    val result : GoldListGoldsRes = webService.listGolds(authorization)
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **authorization** | **kotlin.String**| Bearer token | |

### Return type

[**GoldListGoldsRes**](GoldListGoldsRes.md)

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
val webService = apiClient.createWebservice(GoldApi::class.java)
val authorization : kotlin.String = authorization_example // kotlin.String | Bearer token
val id : kotlin.Int = 56 // kotlin.Int | gold id
val body : GoldUpdateGoldReq =  // GoldUpdateGoldReq | body

launch(Dispatchers.IO) {
    val result : UpdateGoldRes = webService.updateGold(authorization, id, body)
}
```

### Parameters
| **authorization** | **kotlin.String**| Bearer token | |
| **id** | **kotlin.Int**| gold id | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **body** | [**GoldUpdateGoldReq**](GoldUpdateGoldReq.md)| body | |

### Return type

[**UpdateGoldRes**](UpdateGoldRes.md)

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
val webService = apiClient.createWebservice(GoldApi::class.java)
val authorization : kotlin.String = authorization_example // kotlin.String | Bearer token
val id : kotlin.Int = 56 // kotlin.Int | tax preference id
val body : GoldGoldTaxPreferenceReq =  // GoldGoldTaxPreferenceReq | body

launch(Dispatchers.IO) {
    val result : UpdateGoldTaxPreferenceRes = webService.updateGoldTaxPreference(authorization, id, body)
}
```

### Parameters
| **authorization** | **kotlin.String**| Bearer token | |
| **id** | **kotlin.Int**| tax preference id | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **body** | [**GoldGoldTaxPreferenceReq**](GoldGoldTaxPreferenceReq.md)| body | |

### Return type

[**UpdateGoldTaxPreferenceRes**](UpdateGoldTaxPreferenceRes.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

