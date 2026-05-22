# WalletApi

All URIs are relative to *http://localhost:3000/api*

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
val body : WalletCreateWalletReq =  // WalletCreateWalletReq | body

launch(Dispatchers.IO) {
    val result : CreateWalletRes = webService.createWallet(body)
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **body** | [**WalletCreateWalletReq**](WalletCreateWalletReq.md)| body | |

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
val id : kotlin.Int = 56 // kotlin.Int | Wallet ID

launch(Dispatchers.IO) {
    val result : DeleteWalletRes = webService.deleteWallet(id)
}
```

### Parameters
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
val id : kotlin.Int = 56 // kotlin.Int | Wallet ID

launch(Dispatchers.IO) {
    val result : GetWalletRes = webService.getWallet(id)
}
```

### Parameters
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

launch(Dispatchers.IO) {
    val result : WalletListWalletsRes = webService.listWallets()
}
```

### Parameters
This endpoint does not need any parameter.

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
val id : kotlin.Int = 56 // kotlin.Int | Wallet ID
val body : WalletUpdateWalletReq =  // WalletUpdateWalletReq | body

launch(Dispatchers.IO) {
    val result : UpdateWalletRes = webService.updateWallet(id, body)
}
```

### Parameters
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

