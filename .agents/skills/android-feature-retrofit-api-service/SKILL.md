---
name: android-feature-retrofit-api-service
description: Defines how to place Retrofit ApiService, DTOs, mappers, and repositories inside each Android feature's data layer.
---

## Purpose

This skill defines how to organize Retrofit-based API access in an Android project so each feature owns its own data-layer API service.

Use this skill when you want API interfaces, DTOs, repository implementations, and remote data access to live inside `feature/<feature>/data`, while UI code stays in `feature/<feature>/presentation`.

---

## Core Rule

Every feature-specific Retrofit ApiService MUST live inside the feature's `data` folder.

Examples:

- `feature/auth/data`
- `feature/category/data`
- `feature/todo/data`
- `feature/user/data`

Rules:

- keep feature-owned API interfaces in the same feature
- keep Retrofit implementation details out of presentation code
- do not call Retrofit directly from `Screen`, `ViewModel`, or `UiState`

---

## Recommended Data Layer Shape

A feature should usually be organized like this:

```text
feature/
  auth/
    data/
      api/
        AuthApiService.kt
      dto/
        LoginRequest.kt
        LoginResponse.kt
      mapper/
        AuthMapper.kt
      repository/
        AuthRepository.kt
      datasource/
        AuthRemoteDataSource.kt
    presentation/
      login/
        LoginScreen.kt
        LoginVM.kt
        LoginUiState.kt
      register/
        RegisterScreen.kt
        RegisterVM.kt
        RegisterUiState.kt
```

Another example:

```text
feature/
  wallet/
    data/
      api/
        WalletApiService.kt
      dto/
        WalletRequest.kt
        WalletResponse.kt
      repository/
        WalletRepository.kt
    presentation/
      create/
        CreateWalletScreen.kt
        CreateWalletVM.kt
        CreateWalletUiState.kt
```

---

## ApiService Rule

The ApiService file MUST define only the Retrofit interface for that feature.

Rules:

- name it clearly after the feature, such as `AuthApiService` or `WalletApiService`
- keep HTTP annotations and endpoint definitions here
- do not place business logic in the ApiService
- do not place UI logic in the ApiService
- every endpoint MUST return `Response<DTO>` or `Response<Unit>` when no body is returned
- never return DTOs directly from an ApiService method
- import and use `retrofit2.Response` explicitly in the ApiService

Example responsibilities:

- declare `@GET`, `@POST`, `@PUT`, `@DELETE`
- define request and response types
- expose endpoints used by the feature repository

Example:

```kotlin
@POST("api/auth/login")
suspend fun login(@Body request: LoginRequestDto): Response<AuthResponseDto>
```

---

## DTO Rule

DTO files MUST represent network request and response payloads.

Rules:

- keep DTOs in the feature's `data` layer
- use DTOs for network shapes, not UI state
- map DTOs to domain models when needed
- do not expose Retrofit DTOs directly to presentation if a domain model exists

---

## Mapper Rule

Mapper files SHOULD convert between DTOs, domain models, and internal data objects.

Rules:

- keep mapping code in `data`
- map network models to domain models or repository models
- keep mapping logic out of presentation
- prefer explicit mapping functions when payloads are not trivial

---

## Repository Rule

Repository files MUST live in the feature's `data` folder.

Rules:

- repositories call the ApiService or remote datasource
- repositories MUST inspect `Response.isSuccessful` before reading the body
- repositories MUST handle HTTP error codes and missing bodies explicitly
- repositories may combine remote, local, and cache sources
- repositories may return domain objects or result wrappers
- do not let presentation talk directly to Retrofit

Recommended flow:

`Screen -> ViewModel -> Repository -> ApiService`

Recommended repository handling:

- call the ApiService method
- if `response.isSuccessful` is false, convert the error body or status code into a domain-friendly failure
- if `response.body()` is null on success, treat it as an error
- map `Response<DTO>` into domain models before returning to presentation

---

## Shared Retrofit Rule

If Retrofit setup is shared across features, put the shared configuration in `core`.

Examples:

- `core/network/RetrofitClient.kt`
- `core/network/OkHttpClientProvider.kt`
- `core/network/ApiConfig.kt`

Rules:

- keep base URL, client configuration, and interceptors in `core`
- keep feature endpoints in the feature's `data` layer
- if a service is truly shared by multiple features, place it in a shared network package, not in presentation

---

## Feature Boundary Rule

A feature folder SHOULD stay focused on one business area.

Rules:

- put auth endpoints in `feature/auth/data`
- put wallet endpoints in `feature/wallet/data`
- put category endpoints in `feature/category/data`
- put user endpoints in `feature/user/data`
- do not mix unrelated API services inside one feature unless they belong to the same business area

---

## Decision Rules

When deciding where code belongs, use these questions:

1. Is this a Retrofit interface for a specific feature?
   - Put it in `feature/<feature>/data/api`
2. Is this a request or response payload?
   - Put it in `feature/<feature>/data/dto`
3. Is this mapping between DTOs and models?
   - Put it in `feature/<feature>/data/mapper`
4. Is this remote data access or repository logic?
   - Put it in `feature/<feature>/data/datasource` or `feature/<feature>/data/repository`
5. Is this shared Retrofit configuration?
   - Put it in `core/network`
6. Is this screen, VM, or UiState?
   - Put it in `feature/<feature>/presentation`

---

## Quality Criteria

A valid Retrofit feature structure should satisfy all of these:

- every feature-owned ApiService lives in that feature's `data` folder
- every ApiService method returns `Response<DTO>` or `Response<Unit>`
- Retrofit setup is shared only when it is truly cross-feature
- presentation code does not depend on Retrofit directly
- DTOs are separated from UI state
- repositories own the API call flow
- file names clearly identify the feature and responsibility

---

## Completion Check

Before considering the implementation complete, verify:

- the ApiService is inside `feature/<feature>/data`
- every ApiService method returns `Response<DTO>` or `Response<Unit>`
- DTOs are inside `feature/<feature>/data`
- repository logic is inside `feature/<feature>/data`
- presentation only uses repository or use-case level abstractions
- Retrofit client configuration is shared in `core` if reused across features
- file names are feature-specific and descriptive

---

## Example Prompts

Use this skill when you want to:

- create a Retrofit ApiService for a new Android feature
- move networking code from presentation into the data layer
- split shared Retrofit configuration from feature-specific endpoints
- organize feature repositories around Retrofit

Example prompts:

- "Buat AuthApiService di feature auth bagian data"
- "Susun Retrofit wallet supaya repository dan DTO ada di data layer"
- "Pindahkan endpoint category ke feature/category/data"
- "Buat struktur Retrofit per feature yang rapi dan konsisten"

---

## Notes

This skill is intentionally strict.

If a feature has its own API endpoints, the endpoints should live with that feature's data layer.

If a network concern is reused across many features, extract it to `core/network` instead of duplicating it.

If the project later grows, you can extend this skill with more detailed rules for:

- error handling and result wrappers
- caching and local database sync
- authentication headers and token refresh
- DTO to domain mapping conventions
- testing for repositories and ApiService
