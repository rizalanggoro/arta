---
name: golang-feature-repository
description: Guidelines for implementing repository types for features. Repositories perform CRUD via GORM and may return primitive, domain, or dto results; they should be simple concrete types (no interface required).
---

## Purpose

This skill documents the project conventions for feature repository implementations.

Use this when creating or reviewing repository code under `internal/feature/<feature>/repository.go`.

---

## High-level rules

- A repository is a concrete struct type (no interface required).
- Repositories perform database CRUD using GORM models and mapping helpers provided by domain types.
- Repository methods may accept primitive values or `domain` objects as input.
- Repository return values may be primitives, `domain` objects, or `dto` objects (and error).
- For list/query results that may include related entities, prefer returning DTOs directly.
- Repository return values may be primitives, `domain` objects, or `dto` objects (and error).
- Repositories MUST NOT create ad-hoc/anonymous/custom structs to return from methods. Return types must be one of:
  - `(*domain.Type, error)`
  - `(*dto.Type, error)`
  - primitive (e.g., `int`, `bool`) with `error`
- For list/query results that may include related entities, prefer returning DTOs directly.

---

## Initialization pattern

Repository types MUST be initialized with a `*gorm.DB` and follow this pattern:

```go
type WalletRepository struct {
	db *gorm.DB
}

func NewWalletRepository(db *gorm.DB) *WalletRepository {
	return &WalletRepository{db: db}
}
```

Use the same pattern for feature repositories (replace `Wallet` with feature name).

---

## Method behavior

- If a method receives a `domain` value, convert it to `model` using the domain's `ToModel()` helper before DB operations.
- After DB operations, map `model` → `domain` using `From<Model>Model` helpers.
- If a method needs to return rich data combining multiple domains (preload/include), map `model` → `domain` → `dto` and return DTOs from the repository.
- For single-item returns you may return `(*domain.Type, error)` or `(*dto.Type, error)` depending on caller needs.

---

## Examples

Create accepting domain and returning domain:

```go
func (r *WalletRepository) Create(data domain.Wallet) (*domain.Wallet, error) {
	model := data.ToModel()
	if err := r.db.Create(&model).Error; err != nil {
		return nil, err
	}

	return domain.FromWalletModel(model), nil
}
```

List/query returning DTOs (preferred when relations included):

```go
type GetAllFilter struct {
	Keyword         string
	IncludeCategory bool
}

func (r *WalletRepository) GetAll(filter *GetAllFilter) (*[]dto.Wallet, error) {
	var wallets []model.Wallet

	query := r.db
	if filter != nil {
		if filter.IncludeCategory {
			query = query.Preload("Category")
		}
		if filter.Keyword != "" {
			query = query.Where("lower(name) LIKE ?", "%"+strings.ToLower(filter.Keyword)+"%")
		}
	}

	if err := query.Order("lower(name) asc").Find(&wallets).Error; err != nil {
		return nil, err
	}

	result := make([]dto.Wallet, len(wallets))
	for i, wallet := range wallets {
		result[i] = dto.Wallet{
			Data: *domain.FromWalletModel(&wallet),
		}
	}

	return &result, nil
}
```

---

## Return type guidance

- Use `(*domain.Type, error)` when callers need domain-level mapping and no relations/joins are required.
- Use `(*dto.Type, error)` or `(*[]dto.Type, error)` for queries that include relations or when returning composed data is simpler for handlers.
- Returning primitive values (e.g., `int`, `bool`) is allowed for simple operations (counts, existence checks).

IMPORTANT: Do NOT create new struct types inside the repository just to shape a response payload. Always reuse `domain` or `dto` types for return values. The repository should not invent ad-hoc return structs that are not part of the domain/dto layer.

---

## Filters and preload

- Model query options should be expressed via a filter struct passed to repository methods.
- Include flags to control preloads (e.g., `IncludeCategory bool`) in the filter.
- Repositories should only perform the DB query and mapping; business logic belongs to the handler or service layer.

---

## Mapping responsibilities

- Repository: perform DB operations; map `model` → `domain` and optionally `domain` → `dto` for returned values.
- Domain: provide `ToModel()` and `From<Model>Model()` helpers.
- Handler: prefer to consume DTOs returned by repository; if repository returns domains, handler maps domain → dto before embedding into response.

---

## Do not

- Do not create interfaces for every repository unless there's a clear reason (testing/mocking at scale).
- Do not put HTTP/handler logic inside repository implementations.
- Do not add helper functions inside `internal/dto` or `internal/domain` files; keep mapping helpers in `domain` as specified.
- Do not create ad-hoc struct types inside a repository to use as a function return. Use `domain` or `dto` types instead.

---

## Completion checklist

- Repository struct follows `NewXRepository(db *gorm.DB)` pattern
- Methods convert `domain` → `model` before DB writes
- Methods map `model` → `domain` after DB reads
- List/query methods return DTOs when relations included
- Filter structs are used for query options

---
