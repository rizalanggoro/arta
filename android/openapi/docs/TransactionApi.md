# TransactionApi

All URIs are relative to *http://localhost:8080*

| Method | HTTP request | Description |
| ------------- | ------------- | ------------- |
| [**createTransaction**](TransactionApi.md#createTransaction) | **POST** api/transaction |  |
| [**deleteTransaction**](TransactionApi.md#deleteTransaction) | **DELETE** api/transaction/{id} |  |
| [**getTransaction**](TransactionApi.md#getTransaction) | **GET** api/transaction/{id} |  |
| [**listTransactions**](TransactionApi.md#listTransactions) | **GET** api/transaction |  |
| [**updateTransaction**](TransactionApi.md#updateTransaction) | **PUT** api/transaction/{id} |  |





### Example
```kotlin
// Import classes:
//import id.my.rizalanggoro.arta.openapi.*
//import id.my.rizalanggoro.arta.openapi.infrastructure.*
//import id.my.rizalanggoro.arta.openapi.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(TransactionApi::class.java)
val authorization : kotlin.String = authorization_example // kotlin.String | Bearer token
val body : CreateTransactionReq =  // CreateTransactionReq | body

launch(Dispatchers.IO) {
    val result : CreateTransactionRes = webService.createTransaction(authorization, body)
}
```

### Parameters
| **authorization** | **kotlin.String**| Bearer token | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **body** | [**CreateTransactionReq**](CreateTransactionReq.md)| body | |

### Return type

[**CreateTransactionRes**](CreateTransactionRes.md)

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
val webService = apiClient.createWebservice(TransactionApi::class.java)
val authorization : kotlin.String = authorization_example // kotlin.String | Bearer token
val id : kotlin.Int = 56 // kotlin.Int | transaction id

launch(Dispatchers.IO) {
    val result : DeleteTransactionRes = webService.deleteTransaction(authorization, id)
}
```

### Parameters
| **authorization** | **kotlin.String**| Bearer token | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **id** | **kotlin.Int**| transaction id | |

### Return type

[**DeleteTransactionRes**](DeleteTransactionRes.md)

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
val webService = apiClient.createWebservice(TransactionApi::class.java)
val authorization : kotlin.String = authorization_example // kotlin.String | Bearer token
val id : kotlin.Int = 56 // kotlin.Int | transaction id

launch(Dispatchers.IO) {
    val result : GetTransactionRes = webService.getTransaction(authorization, id)
}
```

### Parameters
| **authorization** | **kotlin.String**| Bearer token | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **id** | **kotlin.Int**| transaction id | |

### Return type

[**GetTransactionRes**](GetTransactionRes.md)

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
val webService = apiClient.createWebservice(TransactionApi::class.java)
val authorization : kotlin.String = authorization_example // kotlin.String | Bearer token
val walletId : kotlin.Int = 56 // kotlin.Int | wallet id
val includeCategory : kotlin.Boolean = true // kotlin.Boolean | include_category
val startDate : kotlin.String = startDate_example // kotlin.String | start_date
val endDate : kotlin.String = endDate_example // kotlin.String | end_date

launch(Dispatchers.IO) {
    val result : kotlin.collections.List<DtoTransaction> = webService.listTransactions(authorization, walletId, includeCategory, startDate, endDate)
}
```

### Parameters
| **authorization** | **kotlin.String**| Bearer token | |
| **walletId** | **kotlin.Int**| wallet id | |
| **includeCategory** | **kotlin.Boolean**| include_category | [optional] |
| **startDate** | **kotlin.String**| start_date | [optional] |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **endDate** | **kotlin.String**| end_date | [optional] |

### Return type

[**kotlin.collections.List&lt;DtoTransaction&gt;**](DtoTransaction.md)

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
val webService = apiClient.createWebservice(TransactionApi::class.java)
val authorization : kotlin.String = authorization_example // kotlin.String | Bearer token
val id : kotlin.Int = 56 // kotlin.Int | transaction id
val body : UpdateTransactionReq =  // UpdateTransactionReq | body

launch(Dispatchers.IO) {
    val result : UpdateTransactionRes = webService.updateTransaction(authorization, id, body)
}
```

### Parameters
| **authorization** | **kotlin.String**| Bearer token | |
| **id** | **kotlin.Int**| transaction id | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **body** | [**UpdateTransactionReq**](UpdateTransactionReq.md)| body | |

### Return type

[**UpdateTransactionRes**](UpdateTransactionRes.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

