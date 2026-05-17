package domain

import (
	"time"

	"github.com/artafinance/backend/internal/model"
	"gorm.io/gorm"
)

// Wallet represents a wallet in the domain layer.
// @name Wallet
type Wallet struct {
	ID        uint
	UserID    uint
	Name      string
	Type      string
	CreatedAt time.Time
	UpdatedAt time.Time
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
