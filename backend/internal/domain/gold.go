package domain

import (
	"time"

	"github.com/artafinance/backend/internal/model"
)

// Gold represents gold holdings in the domain layer
// @name Gold
type Gold struct {
	ID        uint      `json:"id"`
	WalletID  uint      `json:"wallet_id"`
	Date      time.Time `json:"date"`
	Grams     float64   `json:"grams"`
	Price     uint64    `json:"price"`
	Type      string    `json:"type"` // pure_gold or jewelry
	Carat     float64   `json:"carat"`
	Notes     string    `json:"notes"`
	CreatedAt time.Time `json:"created_at"`
	UpdatedAt time.Time `json:"updated_at"`
}

const (
	GoldTypePure    = "pure_gold"
	GoldTypeJewelry = "jewelry"
)

// FromGoldModel maps a gold model to the domain layer.
func FromGoldModel(m *model.Gold) *Gold {
	if m == nil {
		return nil
	}

	return &Gold{
		ID:        m.ID,
		WalletID:  m.WalletID,
		Date:      m.Date,
		Grams:     m.Grams,
		Price:     m.Price,
		Type:      m.Type,
		Carat:     m.Carat,
		Notes:     m.Notes,
		CreatedAt: m.CreatedAt,
		UpdatedAt: m.UpdatedAt,
	}
}

// ToModel maps the domain layer to a gold model.
func (g *Gold) ToModel() *model.Gold {
	if g == nil {
		return nil
	}

	return &model.Gold{
		WalletID: g.WalletID,
		Date:     g.Date,
		Grams:    g.Grams,
		Price:    g.Price,
		Type:     g.Type,
		Carat:    g.Carat,
		Notes:    g.Notes,
	}
}
