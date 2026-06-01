package domain

import (
	"time"

	"github.com/artafinance/backend/internal/model"
	"gorm.io/gorm"
)

// GoldTaxPreference represents a user's gold tax preference in the domain layer.
// @name GoldTaxPreference
type GoldTaxPreference struct {
	ID        uint      `json:"id"`
	UserID    uint      `json:"user_id"`
	Carat     float64   `json:"carat" format:"double"`
	TaxRate   float64   `json:"tax_rate" format:"double"`
	CreatedAt time.Time `json:"created_at"`
	UpdatedAt time.Time `json:"updated_at"`
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
