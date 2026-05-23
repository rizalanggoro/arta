---
name: golang-feature-response
description: Defines how to create Go feature response.go files for internal/feature packages. Response structs must use the Res suffix, include @name annotations, and only return DTOs or primitive types from handler.go.
---

## Purpose

This skill defines the standard for response structs inside Go feature folders.

It is used when creating or reviewing `response.go` files under:

- `internal/feature/<feature>/response.go`

The response layer is the output contract for a feature handler.

---

## Rules

### 1. Response Struct Naming

Every response struct MUST end with `Res`.

Each handler route MUST have its own response struct, even when the fields look similar.

Examples:

- `GetAllCategoriesRes`
- `LoginRes`
- `CreateTodoRes`

---

### 2. Swagger / Swaggo Name Annotation

Every response struct MUST include a `//@name ...` annotation.

Example:

```go
type GetAllCategoriesRes struct {
	Items []dto.Category `json:"items"`
} //@name GetAllCategoriesRes
```

Place the annotation on the struct declaration line, matching the project convention.

---

### 3. File Placement

`response.go` MUST live inside the feature folder that owns the API behavior.

Examples:

- `internal/feature/category/response.go`
- `internal/feature/todo/response.go`

---

### 4. Access Rule

Response structs are owned by the feature handler layer.

They MUST be used by `handler.go` for API responses.

If a feature has 5 routes, it MUST have 5 distinct response structs.

They MUST NOT be returned from repository directly.

They MUST NOT be called from anywhere except `handler.go` inside the same feature.

---

### 5. Output Design

Response structs should:

- Expose the final API response body
- Use DTO types when the response contains structured application data
- Use primitive types such as string, number, and bool when the response is simple
- Keep JSON tags aligned with the public API contract

Response structs MUST NOT return domain types.

Response files MUST contain structs only.

Do not place constructor functions, mapping helpers, or other functions inside `response.go`.

Only these are allowed:

- DTO types from `internal/dto`
- Primitive types
- Collections of DTOs or primitive types

---

## Example

```go
package category

import "vibe/internal/dto"

type GetAllCategoriesRes struct {
	Items []dto.Category `json:"items"`
} //@name GetAllCategoriesRes
```

```go
package auth

type LoginRes struct {
	Token string `json:"token"`
} //@name LoginRes
```

---

## Completion Check

A valid feature response file should satisfy all of these:

- Struct names end with `Res`
- Each response struct has a matching `//@name ...` annotation
- File is located in the feature's `response.go`
- Structs are intended for handler output only
- Response fields contain DTOs or primitive types only
- No domain type is returned directly

---

## Common Pattern

For each feature, keep response types separated by use case when needed:

- `CreateTodoRes`
- `ListTodoRes`
- `GetTodoRes`
- `UpdateTodoRes`
- `DeleteTodoRes`
- `LoginRes`

Use one response file per feature, and let `handler.go` be the only layer that builds these response structs before writing them to the client.
