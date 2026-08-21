# WalletApi

All URIs are relative to *http://localhost:8080*

| Method | HTTP request | Description |
| ------------- | ------------- | ------------- |
| [**createWallet**](WalletApi.md#createWallet) | **POST** api/wallet |  |
| [**deleteWallet**](WalletApi.md#deleteWallet) | **DELETE** api/wallet/{id} |  |
| [**getWallet**](WalletApi.md#getWallet) | **GET** api/wallet/{id} |  |
| [**listWallets**](WalletApi.md#listWallets) | **GET** api/wallet |  |
| [**updateWallet**](WalletApi.md#updateWallet) | **PUT** api/wallet/{id} |  |





### Example
```kotlin
// Import classes:
//import id.my.rizalanggoro.arta.openapi.*
//import id.my.rizalanggoro.arta.openapi.infrastructure.*
//import id.my.rizalanggoro.arta.openapi.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(WalletApi::class.java)
val authorization : kotlin.String = authorization_example // kotlin.String | Bearer token
val body : WalletCreateWalletReq =  // WalletCreateWalletReq | body
val idempotencyKey : kotlin.String = idempotencyKey_example // kotlin.String | Unique key per submission attempt for safe retry (UUID recommended)

launch(Dispatchers.IO) {
    val result : CreateWalletRes = webService.createWallet(authorization, body, idempotencyKey)
}
```

### Parameters
| **authorization** | **kotlin.String**| Bearer token | |
| **body** | [**WalletCreateWalletReq**](WalletCreateWalletReq.md)| body | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **idempotencyKey** | **kotlin.String**| Unique key per submission attempt for safe retry (UUID recommended) | [optional] |

### Return type

[**CreateWalletRes**](CreateWalletRes.md)

### Authorization



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
val webService = apiClient.createWebservice(WalletApi::class.java)
val authorization : kotlin.String = authorization_example // kotlin.String | Bearer token
val id : kotlin.Int = 56 // kotlin.Int | Wallet ID

launch(Dispatchers.IO) {
    val result : DeleteWalletRes = webService.deleteWallet(authorization, id)
}
```

### Parameters
| **authorization** | **kotlin.String**| Bearer token | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **id** | **kotlin.Int**| Wallet ID | |

### Return type

[**DeleteWalletRes**](DeleteWalletRes.md)

### Authorization



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
val webService = apiClient.createWebservice(WalletApi::class.java)
val authorization : kotlin.String = authorization_example // kotlin.String | Bearer token
val id : kotlin.Int = 56 // kotlin.Int | Wallet ID

launch(Dispatchers.IO) {
    val result : GetWalletRes = webService.getWallet(authorization, id)
}
```

### Parameters
| **authorization** | **kotlin.String**| Bearer token | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **id** | **kotlin.Int**| Wallet ID | |

### Return type

[**GetWalletRes**](GetWalletRes.md)

### Authorization



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
val webService = apiClient.createWebservice(WalletApi::class.java)
val authorization : kotlin.String = authorization_example // kotlin.String | Bearer token

launch(Dispatchers.IO) {
    val result : WalletListWalletsRes = webService.listWallets(authorization)
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **authorization** | **kotlin.String**| Bearer token | |

### Return type

[**WalletListWalletsRes**](WalletListWalletsRes.md)

### Authorization



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
val webService = apiClient.createWebservice(WalletApi::class.java)
val authorization : kotlin.String = authorization_example // kotlin.String | Bearer token
val id : kotlin.Int = 56 // kotlin.Int | Wallet ID
val body : WalletUpdateWalletReq =  // WalletUpdateWalletReq | body

launch(Dispatchers.IO) {
    val result : UpdateWalletRes = webService.updateWallet(authorization, id, body)
}
```

### Parameters
| **authorization** | **kotlin.String**| Bearer token | |
| **id** | **kotlin.Int**| Wallet ID | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **body** | [**WalletUpdateWalletReq**](WalletUpdateWalletReq.md)| body | |

### Return type

[**UpdateWalletRes**](UpdateWalletRes.md)

### Authorization



### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

