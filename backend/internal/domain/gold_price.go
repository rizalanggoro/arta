package domain

import (
	"time"

	"github.com/artafinance/backend/internal/model"
	"gorm.io/gorm"
)

// GoldPrice represents gold price history in the domain layer
// @name GoldPrice
type GoldPrice struct {
	ID                uint
	Symbol            string
	Currency          string
	CurrencySymbol    string
	ExchangeRate      float64
	PricePerOunceUSD  float64
	SourceUpdatedAt   time.Time
	SourceReadableAge string
	CreatedAt         time.Time
}

// FromGoldPriceModel maps a gold price model to the domain layer.
func FromGoldPriceModel(m *model.GoldPrice) *GoldPrice {
	if m == nil {
		return nil
	}

	return &GoldPrice{
		ID:                m.ID,
		Symbol:            m.Symbol,
		Currency:          m.Currency,
		CurrencySymbol:    m.CurrencySymbol,
		ExchangeRate:      m.ExchangeRate,
		PricePerOunceUSD:  m.PricePerOunceUSD,
		SourceUpdatedAt:   m.SourceUpdatedAt,
		SourceReadableAge: m.SourceReadableAge,
		CreatedAt:         m.CreatedAt,
	}
}

// ToModel maps the domain layer to a gold price model.
func (g *GoldPrice) ToModel() *model.GoldPrice {
	if g == nil {
		return nil
	}

	return &model.GoldPrice{
		Model:             gorm.Model{ID: g.ID},
		Symbol:            g.Symbol,
		Currency:          g.Currency,
		CurrencySymbol:    g.CurrencySymbol,
		ExchangeRate:      g.ExchangeRate,
		PricePerOunceUSD:  g.PricePerOunceUSD,
		SourceUpdatedAt:   g.SourceUpdatedAt,
		SourceReadableAge: g.SourceReadableAge,
	}
}
