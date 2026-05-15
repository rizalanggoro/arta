package domain

import (
	"time"

	"github.com/artafinance/backend/internal/model"
	"gorm.io/gorm"
)

// Transaction represents a financial transaction in the domain layer
// @name Transaction
type Transaction struct {
	ID          uint      `json:"id"`
	WalletID    uint      `json:"wallet_id"`
	Type        string    `json:"type"` // income or expense
	Amount      float64   `json:"amount"`
	CategoryID  uint      `json:"category_id"`
	Description string    `json:"description"`
	Date        time.Time `json:"date"`
	CreatedAt   time.Time `json:"created_at"`
	UpdatedAt   time.Time `json:"updated_at"`
}

// FromTransactionModel maps a transaction model to the domain layer.
func FromTransactionModel(m *model.Transaction) *Transaction {
	if m == nil {
		return nil
	}

	return &Transaction{
		ID:          m.ID,
		WalletID:    m.WalletID,
		Type:        m.Type,
		Amount:      m.Amount,
		CategoryID:  m.CategoryID,
		Description: m.Description,
		Date:        m.Date,
		CreatedAt:   m.CreatedAt,
		UpdatedAt:   m.UpdatedAt,
	}
}

// ToModel maps the domain layer to a transaction model.
func (t *Transaction) ToModel() *model.Transaction {
	if t == nil {
		return nil
	}

	return &model.Transaction{
		Model:       gorm.Model{ID: t.ID},
		WalletID:    t.WalletID,
		Type:        t.Type,
		Amount:      t.Amount,
		CategoryID:  t.CategoryID,
		Description: t.Description,
		Date:        t.Date,
	}
}
