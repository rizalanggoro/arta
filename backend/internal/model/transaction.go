package model

import (
	"time"

	"gorm.io/gorm"
)

// Transaction represents a financial transaction.
type Transaction struct {
	gorm.Model
	WalletID    uint 
	Wallet      Wallet `gorm:"constraint:OnUpdate:CASCADE;OnDelete:CASCADE"`
	CategoryID  uint
	Amount      float64
	Category    Category `gorm:"constraint:OnUpdate:CASCADE;OnDelete:RESTRICT"`
	Description string
	Date        time.Time
	IdempotencyKey *string `gorm:"size:64;uniqueIndex"`

	// Foreign keys
}

// TableName specifies the table name for Transaction model.
func (Transaction) TableName() string {
	return "transactions"
}
