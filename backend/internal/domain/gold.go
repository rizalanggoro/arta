package domain

import (
	"time"

	"github.com/artafinance/backend/internal/model"
	"gorm.io/gorm"
)

// Gold represents gold holdings in the domain layer
// @name Gold
type Gold struct {
	ID            uint
	WalletID      uint
	Date          time.Time
	Grams         float64
	PricePerGram  float64
	TotalValue    float64
	Type          string  // pure_24k, jewelry_ring, etc
	PurityPercent float64 // For reference
	Notes         string
	CreatedAt     time.Time
	UpdatedAt     time.Time
}

// FromGoldModel maps a gold model to the domain layer.
func FromGoldModel(m *model.Gold) *Gold {
	if m == nil {
		return nil
	}

	return &Gold{
		ID:            m.ID,
		WalletID:      m.WalletID,
		Date:          m.Date,
		Grams:         m.Grams,
		PricePerGram:  m.PricePerGram,
		TotalValue:    m.TotalValue,
		Type:          m.Type,
		PurityPercent: m.PurityPercent,
		Notes:         m.Notes,
		CreatedAt:     m.CreatedAt,
		UpdatedAt:     m.UpdatedAt,
	}
}

// ToModel maps the domain layer to a gold model.
func (g *Gold) ToModel() *model.Gold {
	if g == nil {
		return nil
	}

	return &model.Gold{
		Model:         gorm.Model{ID: g.ID},
		WalletID:      g.WalletID,
		Date:          g.Date,
		Grams:         g.Grams,
		PricePerGram:  g.PricePerGram,
		TotalValue:    g.TotalValue,
		Type:          g.Type,
		PurityPercent: g.PurityPercent,
		Notes:         g.Notes,
	}
}
