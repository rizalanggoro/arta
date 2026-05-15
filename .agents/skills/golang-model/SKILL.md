---
name: golang-model
description: Defines database model structure using GORM. Each model represents a database table and must embed gorm.Model as base fields. Used when generating or enforcing Go GORM entity models.
---

## Purpose

This skill defines how database models must be structured in a Go project using GORM.

A model represents a direct mapping to a database table.

---

## Rules

### 1. GORM Base Model

Every model MUST embed `gorm.Model`:

```go
gorm.Model
```

This provides:

- ID
- CreatedAt
- UpdatedAt
- DeletedAt

---

### 2. Model Responsibility

A model is strictly a database representation.

It is used for:

- ORM mapping (GORM)
- Database CRUD operations

It MUST NOT be used for:

- API responses
- DTO layer
- Business logic

---

### 3. Structure Rules

- Each struct represents one database table
- Field names map to table columns
- Use proper GORM tags for relations and constraints
- JSON tags may exist but are not used for API response layer

---

### 4. Relationships

Relationships must use GORM tags explicitly.

Example:

- `constraint:OnUpdate:CASCADE,OnDelete:CASCADE`

---

### 5. Example Model

```go
import (
	"time"

	"gorm.io/gorm"
)

type Gold struct {
	gorm.Model

	UserId   uint      `json:"user_id"`
	User     User      `gorm:"constraint:OnUpdate:CASCADE,OnDelete:CASCADE;"`
	Weight   float64   `json:"weight"`
	BuyPrice float64   `json:"buy_price"`
	Carat    float64   `json:"carat"`
	Date     time.Time `json:"date"`
}
```

---

## Design Constraints

- MUST embed `gorm.Model`
- MUST represent a single database table
- MUST NOT contain business logic
- MUST NOT depend on DTO or domain layers
- MUST only be used inside repository layer
