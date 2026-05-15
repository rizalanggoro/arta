---
name: golang-feature-dto
description: Defines how to create Go feature dto files for internal/dto packages. DTO files must contain struct-only declarations that represent domain objects by embedding domain types, and include @name annotations.
---

## Purpose

This skill standardizes DTO creation for the project. DTOs are the API-visible representation of domain data and live under `internal/dto`.

Use this skill when creating or reviewing `dto` files for features.

---

## Rules

### 1. File Placement

DTO files MUST live under `internal/dto`.

Example:

- `internal/dto/todo.go`

---

### 2. Struct-only

DTO files MUST contain structs only. They MUST NOT contain functions, constructors, helpers, or other code.

---

### 3. Representation by embedding domain

Each DTO struct represents one or more domain types by embedding them (composition). The DTO struct name usually matches the domain name.

When a DTO uses exactly the same domain type name, embed the domain value under the field named `Data`.

Example (single domain embed):

```go
package dto

import "vibe/internal/domain"

type Category struct {
    Data domain.Category `json:"data"`
} //@name Category
```

When a DTO needs to combine multiple domains, embed each domain using explicit field names matching the domain they represent. Example: DTO `Todo` that embeds both `domain.Todo` and `domain.Category`:

```go
package dto

import "vibe/internal/domain"

type Todo struct {
    Data     domain.Todo     `json:"data"`
    Category domain.Category `json:"category"`
} //@name Todo
```

### 3.1 Repository and handler usage

Repositories MAY return DTO instances directly when that avoids mapping work. Handlers MAY also construct DTOs from domain values.

Handler responsibility:

- Prefer a DTO returned by the repository when present.
- If the repository returns a domain value, convert it to the DTO inside the handler before embedding into the response struct.

Response construction:

- Handlers embed DTOs into response structs defined in `internal/feature/<feature>/response.go`.
- DTO files must remain struct-only; any mapping logic belongs in repository or handler, not in `internal/dto`.

---

### 4. Field visibility

- Do not duplicate fields from the domain inside DTO; embed domain structs instead.
- Use JSON tags on the embedded fields as shown above.

---

### 5. Naming and annotation

- DTO type names should be descriptive and usually mirror the domain name.
- Every DTO struct MUST include a `// @name ...` annotation on the struct declaration line.

---

## Completion checklist

A valid DTO file must satisfy:

- Contains only `type` struct declarations (no functions)
- Each DTO embeds one or more `domain` types (no primitive-only DTOs unless intentionally designed)
- Each DTO has a `// @name ...` annotation on the declaration line
- File is placed under `internal/dto`

---

## Examples

Single-domain DTO:

```go
package dto

import "vibe/internal/domain"

type Category struct {
	Data domain.Category `json:"data"`
} //@name Category
```

Multi-domain DTO:

```go
package dto

import "vibe/internal/domain"

type Todo struct {
	Data     domain.Todo     `json:"data"`
	Category domain.Category `json:"category"`
} //@name Todo
```

---

## Notes and rationale

- Keeping DTOs as embedded domain wrappers avoids copying field definitions and keeps a clear source-of-truth (`domain`).
- Struct-only DTO files are easier to audit for API surface and to generate accurate Swagger/OpenAPI annotations.

---

## Suggested prompt examples

- "Create DTO for `domain.User` following project conventions."
- "Generate `Todo` DTO that embeds `domain.Todo` and `domain.Category`."
