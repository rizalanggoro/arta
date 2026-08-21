# DashboardApi

All URIs are relative to *http://localhost:8080*

| Method | HTTP request | Description |
| ------------- | ------------- | ------------- |
| [**getCashDashboard**](DashboardApi.md#getCashDashboard) | **GET** api/dashboard/cash |  |
| [**getGoldDashboard**](DashboardApi.md#getGoldDashboard) | **GET** api/dashboard/gold |  |
| [**getPriceHistory**](DashboardApi.md#getPriceHistory) | **GET** api/dashboard/price-history |  |





### Example
```kotlin
// Import classes:
//import id.my.rizalanggoro.arta.openapi.*
//import id.my.rizalanggoro.arta.openapi.infrastructure.*
//import id.my.rizalanggoro.arta.openapi.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(DashboardApi::class.java)
val authorization : kotlin.String = authorization_example // kotlin.String | Bearer token
val walletId : kotlin.Int = 56 // kotlin.Int | wallet_id
val startDate : kotlin.String = startDate_example // kotlin.String | start_date
val endDate : kotlin.String = endDate_example // kotlin.String | end_date

launch(Dispatchers.IO) {
    val result : CashDashboardRes = webService.getCashDashboard(authorization, walletId, startDate, endDate)
}
```

### Parameters
| **authorization** | **kotlin.String**| Bearer token | |
| **walletId** | **kotlin.Int**| wallet_id | |
| **startDate** | **kotlin.String**| start_date | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **endDate** | **kotlin.String**| end_date | |

### Return type

[**CashDashboardRes**](CashDashboardRes.md)

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
val webService = apiClient.createWebservice(DashboardApi::class.java)
val authorization : kotlin.String = authorization_example // kotlin.String | Bearer token
val walletId : kotlin.Int = 56 // kotlin.Int | wallet_id

launch(Dispatchers.IO) {
    val result : GoldDashboardRes = webService.getGoldDashboard(authorization, walletId)
}
```

### Parameters
| **authorization** | **kotlin.String**| Bearer token | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **walletId** | **kotlin.Int**| wallet_id | |

### Return type

[**GoldDashboardRes**](GoldDashboardRes.md)

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
val webService = apiClient.createWebservice(DashboardApi::class.java)
val authorization : kotlin.String = authorization_example // kotlin.String | Bearer token
val type : kotlin.String = type_example // kotlin.String | price type: gold or fx
val days : kotlin.Int = 56 // kotlin.Int | number of days of history (default 7)

launch(Dispatchers.IO) {
    val result : PriceHistoryRes = webService.getPriceHistory(authorization, type, days)
}
```

### Parameters
| **authorization** | **kotlin.String**| Bearer token | |
| **type** | **kotlin.String**| price type: gold or fx | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **days** | **kotlin.Int**| number of days of history (default 7) | [optional] |

### Return type

[**PriceHistoryRes**](PriceHistoryRes.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

