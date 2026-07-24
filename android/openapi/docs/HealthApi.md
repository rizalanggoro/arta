# HealthApi

All URIs are relative to *http://localhost:8080*

| Method | HTTP request | Description |
| ------------- | ------------- | ------------- |
| [**apiHealthGet**](HealthApi.md#apiHealthGet) | **GET** api/health | Health check |



Health check

Return server health status and app version.

### Example
```kotlin
// Import classes:
//import id.my.rizalanggoro.arta.openapi.*
//import id.my.rizalanggoro.arta.openapi.infrastructure.*
//import id.my.rizalanggoro.arta.openapi.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(HealthApi::class.java)

launch(Dispatchers.IO) {
    val result : HealthHealthRes = webService.apiHealthGet()
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**HealthHealthRes**](HealthHealthRes.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

