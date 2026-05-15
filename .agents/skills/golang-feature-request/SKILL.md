---
name: golang-feature-request
description: Defines how to create Go feature request.go files for internal/feature packages. Request structs must use the Req suffix, include @name annotations, and be consumed only by handler.go within the feature.
---

## Purpose

This skill defines the standard for request structs inside Go feature folders.

It is used when creating or reviewing `request.go` files under:

- `internal/feature/<feature>/request.go`

The request layer is the input contract for a feature handler.

---

## Rules

### 1. Request Struct Naming

Every request struct MUST end with `Req`.

Examples:

- `CreateCategoryReq`
- `UpdateTodoReq`
- `LoginReq`

---

### 2. Swagger / Swaggo Name Annotation

Every request struct MUST include a `// @name ...` annotation.

Example:

```go
type CreateCategoryReq struct {
	WalletId uint               `json:"wallet_id"`
	Name     string             `json:"name"`
	Type     model.CategoryType `json:"type"`
} //@name CreateCategoryReq
```

Place the annotation on the struct declaration line, matching the project convention.

---

### 3. File Placement

`request.go` MUST live inside the feature folder that owns the API behavior.

Examples:

- `internal/feature/category/request.go`
- `internal/feature/todo/request.go`

---

### 4. Access Rule

Request structs are owned by the feature handler layer.

They MUST be used by `handler.go` for request decoding and validation.

They SHOULD NOT be used as domain objects or repository inputs directly.

---

### 5. Field Design

Request structs should:

- Match incoming JSON payloads
- Use JSON tags for request body fields
- Use model or primitive types only when appropriate
- Keep validation-friendly names and structure

If a field comes from a model enum or shared type, it may use that type directly.

---

## Example

```go
package category

import "vibe/internal/model"

type CreateCategoryReq struct {
	WalletId uint               `json:"wallet_id"`
	Name     string             `json:"name"`
	Type     model.CategoryType `json:"type"`
} //@name CreateCategoryReq
```

---

## Completion Check

A valid feature request file should satisfy all of these:

- Struct names end with `Req`
- Each request struct has a matching `// @name ...` annotation
- File is located in the feature's `request.go`
- Structs are intended for handler input only
- Payload fields match the expected JSON contract

---

## Common Pattern

For each feature, keep request types separated by use case when needed:

- `CreateTodoReq`
- `UpdateTodoReq`
- `ListTodoReq`

Use one request file per feature, and let `handler.go` be the only layer that decodes these request structs from HTTP input.
