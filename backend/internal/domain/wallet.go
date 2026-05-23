package domain

import (
	"time"

	"github.com/artafinance/backend/internal/model"
	"gorm.io/gorm"
)

// Wallet represents a wallet in the domain layer.
// @name Wallet
type Wallet struct {
	ID        uint      `json:"id"`
	UserID    uint      `json:"user_id"`
	Name      string    `json:"name"`
	Type      string    `json:"type"`
	CreatedAt time.Time `json:"created_at"`
	UpdatedAt time.Time `json:"updated_at"`
}

// FromWalletModel maps a wallet model to the domain layer.
func FromWalletModel(m *model.Wallet) *Wallet {
	if m == nil {
		return nil
	}

	return &Wallet{
		ID:        m.ID,
		UserID:    m.UserID,
		Name:      m.Name,
		Type:      m.Type,
		CreatedAt: m.CreatedAt,
		UpdatedAt: m.UpdatedAt,
	}
}

// ToModel maps the domain layer to a wallet model.
func (w *Wallet) ToModel() *model.Wallet {
	if w == nil {
		return nil
	}

	return &model.Wallet{
		Model:  gorm.Model{ID: w.ID},
		UserID: w.UserID,
		Name:   w.Name,
		Type:   w.Type,
	}
}
