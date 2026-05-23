package domain

import (
	"time"

	"github.com/artafinance/backend/internal/model"
	"gorm.io/gorm"
)

// FxRate represents a foreign exchange rate snapshot in the domain layer.
// @name FxRate
type FxRate struct {
	ID        uint      `json:"id"`
	Base      string    `json:"base"`
	Date      time.Time `json:"date"`
	Rate      int       `json:"rate"`
	CreatedAt time.Time `json:"created_at"`
}

// FromFxRateModel maps an FX rate model to the domain layer.
func FromFxRateModel(m *model.FxRate) *FxRate {
	if m == nil {
		return nil
	}

	return &FxRate{
		ID:        m.ID,
		Base:      m.Base,
		Date:      m.Date,
		Rate:      m.Rate,
		CreatedAt: m.CreatedAt,
	}
}

// ToModel maps the domain layer to an FX rate model.
func (f *FxRate) ToModel() *model.FxRate {
	if f == nil {
		return nil
	}

	return &model.FxRate{
		Model: gorm.Model{ID: f.ID},
		Base:  f.Base,
		Date:  f.Date,
		Rate:  f.Rate,
	}
}
