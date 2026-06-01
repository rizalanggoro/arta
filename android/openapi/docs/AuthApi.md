# AuthApi

All URIs are relative to *http://localhost:3000/api*

| Method | HTTP request | Description |
| ------------- | ------------- | ------------- |
| [**apiAuthLoginPost**](AuthApi.md#apiAuthLoginPost) | **POST** api/auth/login | Login a user |
| [**apiAuthMeGet**](AuthApi.md#apiAuthMeGet) | **GET** api/auth/me | Get current user |
| [**apiAuthRegisterPost**](AuthApi.md#apiAuthRegisterPost) | **POST** api/auth/register | Register a new user |
| [**logout**](AuthApi.md#logout) | **POST** api/auth/logout | Logout current session |



Login a user

Validate credentials and issue a session token.

### Example
```kotlin
// Import classes:
//import id.my.rizalanggoro.arta.openapi.*
//import id.my.rizalanggoro.arta.openapi.infrastructure.*
//import id.my.rizalanggoro.arta.openapi.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(AuthApi::class.java)
val body : LoginReq =  // LoginReq | body

launch(Dispatchers.IO) {
    val result : LoginRes = webService.apiAuthLoginPost(body)
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **body** | [**LoginReq**](LoginReq.md)| body | |

### Return type

[**LoginRes**](LoginRes.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


Get current user

Return the authenticated user&#39;s profile.

### Example
```kotlin
// Import classes:
//import id.my.rizalanggoro.arta.openapi.*
//import id.my.rizalanggoro.arta.openapi.infrastructure.*
//import id.my.rizalanggoro.arta.openapi.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(AuthApi::class.java)
val authorization : kotlin.String = authorization_example // kotlin.String | Bearer token

launch(Dispatchers.IO) {
    val result : MeRes = webService.apiAuthMeGet(authorization)
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **authorization** | **kotlin.String**| Bearer token | |

### Return type

[**MeRes**](MeRes.md)

### Authorization



### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


Register a new user

Create a new account and issue the first session token.

### Example
```kotlin
// Import classes:
//import id.my.rizalanggoro.arta.openapi.*
//import id.my.rizalanggoro.arta.openapi.infrastructure.*
//import id.my.rizalanggoro.arta.openapi.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(AuthApi::class.java)
val body : RegisterReq =  // RegisterReq | body

launch(Dispatchers.IO) {
    val result : RegisterRes = webService.apiAuthRegisterPost(body)
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **body** | [**RegisterReq**](RegisterReq.md)| body | |

### Return type

[**RegisterRes**](RegisterRes.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


Logout current session

Delete the current session token.

### Example
```kotlin
// Import classes:
//import id.my.rizalanggoro.arta.openapi.*
//import id.my.rizalanggoro.arta.openapi.infrastructure.*
//import id.my.rizalanggoro.arta.openapi.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(AuthApi::class.java)
val authorization : kotlin.String = authorization_example // kotlin.String | Bearer token

launch(Dispatchers.IO) {
    val result : LogoutRes = webService.logout(authorization)
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **authorization** | **kotlin.String**| Bearer token | |

### Return type

[**LogoutRes**](LogoutRes.md)

### Authorization



### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

