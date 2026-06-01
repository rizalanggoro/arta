# ReleaseApi

All URIs are relative to *http://localhost:8080*

| Method | HTTP request | Description |
| ------------- | ------------- | ------------- |
| [**apiReleaseLatestGet**](ReleaseApi.md#apiReleaseLatestGet) | **GET** api/release/latest | Get latest release |
| [**apiReleasePost**](ReleaseApi.md#apiReleasePost) | **POST** api/release | Create a new release |



Get latest release

Return the release with the highest version code.

### Example
```kotlin
// Import classes:
//import id.my.rizalanggoro.arta.openapi.*
//import id.my.rizalanggoro.arta.openapi.infrastructure.*
//import id.my.rizalanggoro.arta.openapi.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(ReleaseApi::class.java)

launch(Dispatchers.IO) {
    val result : ReleaseRes = webService.apiReleaseLatestGet()
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**ReleaseRes**](ReleaseRes.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


Create a new release

Store a new APK release url and its version code.

### Example
```kotlin
// Import classes:
//import id.my.rizalanggoro.arta.openapi.*
//import id.my.rizalanggoro.arta.openapi.infrastructure.*
//import id.my.rizalanggoro.arta.openapi.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(ReleaseApi::class.java)
val body : CreateReleaseReq =  // CreateReleaseReq | body

launch(Dispatchers.IO) {
    val result : ReleaseRes = webService.apiReleasePost(body)
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **body** | [**CreateReleaseReq**](CreateReleaseReq.md)| body | |

### Return type

[**ReleaseRes**](ReleaseRes.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

