# DashboardApi

All URIs are relative to *http://localhost:3000/api*

| Method | HTTP request | Description |
| ------------- | ------------- | ------------- |
| [**getCashDashboard**](DashboardApi.md#getCashDashboard) | **GET** api/dashboard/cash | Get cash dashboard overview |
| [**getGoldDashboard**](DashboardApi.md#getGoldDashboard) | **GET** api/dashboard/gold | Get gold dashboard overview |



Get cash dashboard overview

Return the active cash wallet name, balance summary, today totals, and the latest 5 transactions.

### Example
```kotlin
// Import classes:
//import id.my.rizalanggoro.arta.openapi.*
//import id.my.rizalanggoro.arta.openapi.infrastructure.*
//import id.my.rizalanggoro.arta.openapi.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(DashboardApi::class.java)
val authorization : kotlin.String = authorization_example // kotlin.String | Bearer token
val walletId : kotlin.Int = 56 // kotlin.Int | Selected cash wallet ID

launch(Dispatchers.IO) {
    val result : CashDashboardRes = webService.getCashDashboard(authorization, walletId)
}
```

### Parameters
| **authorization** | **kotlin.String**| Bearer token | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **walletId** | **kotlin.Int**| Selected cash wallet ID | [optional] |

### Return type

[**CashDashboardRes**](CashDashboardRes.md)

### Authorization



### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


Get gold dashboard overview

Return the active gold wallet name, asset summary, current prices, and the latest 5 gold entries.

### Example
```kotlin
// Import classes:
//import id.my.rizalanggoro.arta.openapi.*
//import id.my.rizalanggoro.arta.openapi.infrastructure.*
//import id.my.rizalanggoro.arta.openapi.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(DashboardApi::class.java)
val authorization : kotlin.String = authorization_example // kotlin.String | Bearer token

launch(Dispatchers.IO) {
    val result : GoldDashboardRes = webService.getGoldDashboard(authorization)
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **authorization** | **kotlin.String**| Bearer token | |

### Return type

[**GoldDashboardRes**](GoldDashboardRes.md)

### Authorization



### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

