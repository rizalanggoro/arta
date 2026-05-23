---
name: golang-feature-handler
description: Defines how to create Go feature handler.go files for internal/feature packages using Fiber. Handlers must use private route methods, RegisterRoutes, swaggo comments, and only pass domain objects or primitives to repositories.
---

## Purpose

This skill defines the standard for `handler.go` inside Go feature folders.

Use it when creating or reviewing handler code under:

- `internal/feature/<feature>/handler.go`

The handler layer owns HTTP routing, request parsing, repository calls, and response assembly.

---

## Rules

### 1. Framework Rule

Feature handlers MUST use Fiber.

Use:

- `fiber.Router` for route registration
- `*fiber.Ctx` for request handlers

Do not mix `net/http` handlers into this feature pattern.

---

### 2. Handler Struct Pattern

Handlers SHOULD be initialized with concrete repository dependencies.

Example:

```go
type WalletHandler struct {
	walletRepo  *WalletRepository
	sessionRepo *repositories.SessionRepository
}

func NewWalletHandler(
	repo *WalletRepository,
	sessionRepo *repositories.SessionRepository,
) *WalletHandler {
	return &WalletHandler{
		walletRepo:  repo,
		sessionRepo: sessionRepo,
	}
}
```

Rules:

- Use concrete repository types, not interfaces, unless there is a strong reason otherwise
- Keep dependency names feature-specific
- Inject middleware dependencies through the handler when needed

---

### 3. Route Registration

Every handler MUST expose a `RegisterRoutes` method.

Example:

```go
func (h *WalletHandler) RegisterRoutes(router fiber.Router) {
	group := router.Group("/wallets").Use(auth.AuthMiddleware(h.sessionRepo))
	group.Post("/", h.createWallet)
	group.Get("/", h.getAllWallets)
}
```

Rules:

- Route methods are registered only in `RegisterRoutes`
- Middleware may be attached inside `RegisterRoutes`
- Route handlers MUST be private methods
- Keep route grouping close to the feature path and middleware rules

---

### 4. Private Handler Methods

Every route handler MUST use a lowercase function name.

Examples:

- `createWallet`
- `getAllWallets`
- `updateTodo`
- `deleteCategory`

These methods are private implementation details and MUST only be called from `RegisterRoutes`.

`handler.go` MUST NOT contain any additional helper functions beyond:

- `RegisterRoutes`
- the private route handler methods themselves

Do not add utility functions, parser helpers, mapper helpers, or error helpers inside `handler.go`.
If shared behavior is needed, move it to another package or keep the logic inline inside the handler method.

---

### 5. Swagger / Swaggo Comments

Every handler method MUST have swaggo comments above it.

Required comment fields:

- `@id`
- `@tags`
- `@accept`
- `@produce`
- `@param`
- `@success`
- `@router`

Use the public API path and HTTP method in the router annotation.

Example:

```go
// @id                   CreateWallet
// @tags                 wallet
// @accept               json
// @produce              json
// @param                body body CreateWalletReq true "body"
// @success              200 {object} CreateWalletRes
// @router               /api/v1/wallets [post]
func (h *WalletHandler) createWallet(c *fiber.Ctx) error {
	...
}
```

Add `@failure` if the endpoint documents error responses in the project, but keep the required fields above on every handler.

After adding or changing any swagger comment, always regenerate the swaggo output so the documentation stays in sync with the handler annotations.

Use the project standard swaggo generation command or equivalent documented workflow for the workspace.

---

### 6. Request Flow

Handlers MUST parse input from `request.go` structs.

Rules:

- Use request types from the same feature folder
- Parse with `c.BodyParser(...)` or other Fiber request helpers
- Validate parse errors early
- Do not pass request structs directly into repository methods

The handler should transform request data into either:

- primitive values, or
- domain objects

before calling the repository.

---

### 7. Repository Call Boundary

Handlers MAY pass only these values into repositories:

- primitive values
- domain objects

Do not pass DTOs, response structs, or ad-hoc wrapper structs to repositories.

If the repository needs multiple fields, build a domain object in the handler first.

---

### 8. Response Assembly

Handlers MUST return response structs from `response.go`.

Rules:

- Success responses should use the matching `Res` struct
- Response structs may contain DTOs or primitive fields
- Handlers should build the response payload before calling `c.JSON(...)`
- Do not return domain objects directly from the handler if the project expects DTO-based output

Example success response:

```go
return c.Status(fiber.StatusOK).JSON(CreateWalletRes{
	Wallet: *wallet,
})
```

---

### 9. Error Response Pattern

If an error occurs, return the project standard Fiber error payload.

Example:

```go
return c.Status(fiber.StatusInternalServerError).JSON(fiber.Error{
	Code:    fiber.StatusInternalServerError,
	Message: err.Error(),
})
```

Every `fiber.Error` returned by a handler MUST set both `Code` and `Message`.

For request validation or parse failures, use the appropriate Fiber status code if the feature defines it, and keep the response body shape consistent with the project standard.

Examples:

```go
return c.Status(fiber.StatusBadRequest).JSON(fiber.Error{
	Code:    fiber.StatusBadRequest,
	Message: err.Error(),
})
```

```go
return c.Status(fiber.StatusNotFound).JSON(fiber.Error{
	Code:    fiber.StatusNotFound,
	Message: "todo not found",
})
```

---

### 10. Middleware Placement

If a feature needs middleware, place it inside `RegisterRoutes`.

Examples:

- auth middleware
- role checks
- request guards

Do not put route middleware setup inside the route method bodies.

---

## Example

```go
package wallet

import (
	"github.com/gofiber/fiber/v2"
	"vibe/internal/feature/auth"
	"vibe/internal/repositories"
)

type WalletHandler struct {
	walletRepo  *WalletRepository
	sessionRepo *repositories.SessionRepository
}

func NewWalletHandler(
	repo *WalletRepository,
	sessionRepo *repositories.SessionRepository,
) *WalletHandler {
	return &WalletHandler{
		walletRepo:  repo,
		sessionRepo: sessionRepo,
	}
}

func (h *WalletHandler) RegisterRoutes(router fiber.Router) {
	group := router.Group("/wallets").Use(auth.AuthMiddleware(h.sessionRepo))
	group.Post("/", h.createWallet)
	group.Get("/", h.getAllWallets)
}

// @id                   CreateWallet
// @tags                 wallet
// @accept               json
// @produce              json
// @param                body body CreateWalletReq true "body"
// @success              200 {object} CreateWalletRes
// @router               /api/v1/wallets [post]
func (h *WalletHandler) createWallet(c *fiber.Ctx) error {
	var req CreateWalletReq
	if err := c.BodyParser(&req); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Error{Message: err.Error()})
	}

	wallet, err := h.walletRepo.Create(domain.Wallet{
		Name: req.Name,
		Type: req.Type,
	})
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Error{Message: err.Error()})
	}

	return c.Status(fiber.StatusOK).JSON(CreateWalletRes{Wallet: *wallet})
}
```

---

## Completion Check

A valid feature handler should satisfy all of these:

- Uses Fiber routing and context types
- Has a constructor that injects repositories and other dependencies
- Exposes `RegisterRoutes(router fiber.Router)`
- Uses private handler methods for each route
- Adds swaggo comments above every handler method
- Parses request structs from `request.go`
- Passes only primitives or domain objects into repositories
- Returns response structs from `response.go`
- Uses the standard Fiber error payload on failure
- Places middleware inside `RegisterRoutes`

---

## Common Pattern

Use this skill for feature handlers such as:

- `wallet`
- `todo`
- `category`
- `auth`

The handler should stay thin: parse input, call repository, map data if needed, and return the feature response struct.
