---
name: golang-domain
description: Defines domain layer in Go architecture. Domain acts as a pure application data representation used for mapping between model and DTO, and for data transfer between handler and repository. Each domain must map 1:1 with model.
---

## Purpose

The `domain` layer is a clean representation of application data used inside business flow.

It acts as:

- Mapping layer from `model` → `domain`
- Mapping layer from `domain` → `model`
- Intermediate data carrier between handler and repository
- Optional embedded field inside DTO

---

## Rules

### 1. One-to-One Mapping

Each domain MUST correspond to exactly one model:

- `model.Gold` → `domain.Gold`
- Number of domain structs MUST equal number of model structs

---

### 2. No Database Dependency

Domain MUST NOT:

- Import database packages (except `model`)
- Contain ORM tags (GORM tags)
- Perform database operations

Allowed dependency:

- `internal/model` only (for mapping)

---

### 3. Role of Domain

Domain is used for:

- Data transfer between layers
- Business-level representation
- Handler → repository communication
- Repository → handler communication

---

### 4. DTO Integration Rule

Domain MAY be embedded inside DTO structs.

But:

- Domain MUST NOT be returned directly from handler
- DTO is still the final response layer

---

### 5. Mapping Functions Required

Each domain MUST provide:

#### a. Model → Domain

```go
From<Model>Name(m *model.<Model>) *<Domain>
```

#### b. Domain → Model

```go
ToModel() *model.<Model>
```

---

## Example Domain

```go id="domain-gold"
package domain

import (
	"rupia/internal/model"
	"time"
)

type Gold struct {
	Id        uint      `json:"id"`
	UserId    uint      `json:"user_id"`
	Weight    float64   `json:"weight" format:"double"`
	BuyPrice  float64   `json:"buy_price" format:"double"`
	Carat     float64   `json:"carat" format:"double"`
	Date      time.Time `json:"date"`
	CreatedAt time.Time `json:"created_at"`
	UpdatedAt time.Time `json:"updated_at"`
} // @name Gold

func FromGoldModel(m *model.Gold) *Gold {
	return &Gold{
		Id:        m.ID,
		UserId:    m.UserId,
		Weight:    m.Weight,
		BuyPrice:  m.BuyPrice,
		Carat:     m.Carat,
		Date:      m.Date,
		CreatedAt: m.CreatedAt,
		UpdatedAt: m.UpdatedAt,
	}
}

func (m *Gold) ToModel() *model.Gold {
	return &model.Gold{
		UserId:   m.UserId,
		Weight:   m.Weight,
		BuyPrice: m.BuyPrice,
		Carat:    m.Carat,
		Date:     m.Date,
	}
}
```

---

## Constraints

- MUST be used for data mapping only
- MUST NOT contain business logic
- MUST NOT access database directly
- MUST map strictly with model (1:1 structure)
- MUST be safe for use in handler and repository layers
