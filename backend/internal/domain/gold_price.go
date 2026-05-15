package domain

import (
	"time"

	"github.com/artafinance/backend/internal/model"
	"gorm.io/gorm"
)

// GoldPrice represents gold price history in the domain layer
// @name GoldPrice
type GoldPrice struct {
	ID           uint
	Date         time.Time
	PricePerGram float64
	Currency     string
	CreatedAt    time.Time
}

// FromGoldPriceModel maps a gold price model to the domain layer.
func FromGoldPriceModel(m *model.GoldPrice) *GoldPrice {
	if m == nil {
		return nil
	}

	return &GoldPrice{
		ID:           m.ID,
		Date:         m.Date,
		PricePerGram: m.PricePerGram,
		Currency:     m.Currency,
		CreatedAt:    m.CreatedAt,
	}
}

// ToModel maps the domain layer to a gold price model.
func (g *GoldPrice) ToModel() *model.GoldPrice {
	if g == nil {
		return nil
	}

	return &model.GoldPrice{
		Model:        gorm.Model{ID: g.ID},
		Date:         g.Date,
		PricePerGram: g.PricePerGram,
		Currency:     g.Currency,
	}
}
