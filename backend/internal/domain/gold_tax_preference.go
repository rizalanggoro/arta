package domain

import (
	"time"

	"github.com/artafinance/backend/internal/model"
	"gorm.io/gorm"
)

// GoldTaxPreference represents a user's gold tax preference in the domain layer.
// @name GoldTaxPreference
type GoldTaxPreference struct {
	ID        uint
	UserID    uint
	Carat     float64
	TaxRate   float64
	CreatedAt time.Time
	UpdatedAt time.Time
}

// FromGoldTaxPreferenceModel maps a gold tax preference model to the domain layer.
func FromGoldTaxPreferenceModel(m *model.GoldTaxPreference) *GoldTaxPreference {
	if m == nil {
		return nil
	}

	return &GoldTaxPreference{
		ID:        m.ID,
		UserID:    m.UserID,
		Carat:     m.Carat,
		TaxRate:   m.TaxRate,
		CreatedAt: m.CreatedAt,
		UpdatedAt: m.UpdatedAt,
	}
}

// ToModel maps the domain layer to a gold tax preference model.
func (g *GoldTaxPreference) ToModel() *model.GoldTaxPreference {
	if g == nil {
		return nil
	}

	return &model.GoldTaxPreference{
		Model:   gorm.Model{ID: g.ID},
		UserID:  g.UserID,
		Carat:   g.Carat,
		TaxRate: g.TaxRate,
	}
}
